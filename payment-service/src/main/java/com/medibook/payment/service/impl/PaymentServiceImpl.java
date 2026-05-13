package com.medibook.payment.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.medibook.payment.config.RabbitMQConfig;
import com.medibook.payment.dto.NotificationEventDTO;
import com.medibook.payment.dto.PaymentRequestDTO;
import com.medibook.payment.dto.PaymentResponseDTO;
import com.medibook.payment.dto.RazorpayVerifyRequestDTO;
import com.medibook.payment.entity.Payment;
import com.medibook.payment.exception.BadRequestException;
import com.medibook.payment.exception.ResourceNotFoundException;
import com.medibook.payment.repository.PaymentRepository;
import com.medibook.payment.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final RazorpayClient razorpayClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${razorpay.key-secret}")
    private String razorpaySecret;

    @Override
    public PaymentResponseDTO createPaymentRecord(PaymentRequestDTO request) {
        if (request.getAppointmentId() == null) {
            throw new BadRequestException("AppointmentId is required");
        }

        Payment payment = findExistingPaymentForRecord(request).orElseGet(Payment::new);
        payment.setAppointmentId(request.getAppointmentId());
        payment.setPatientId(request.getPatientId());
        payment.setProviderId(request.getProviderId());
        if (request.getSlotId() != null) {
            payment.setSlotId(request.getSlotId());
        }
        payment.setAmount(request.getAmount());
        payment.setMode(resolveMode(request).toUpperCase());
        payment.setCurrency(request.getCurrency() == null ? "INR" : request.getCurrency().toUpperCase());
        payment.setStatus(request.getStatus() == null ? "PAID" : request.getStatus().toUpperCase());

        String transactionId = request.getTransactionId() != null
                ? request.getTransactionId()
                : request.getRazorpayPaymentId();
        payment.setTransactionId(transactionId);
        if (request.getRazorpayPaymentId() != null) {
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        }
        if ("PAID".equalsIgnoreCase(payment.getStatus()) || "SUCCESS".equalsIgnoreCase(payment.getStatus())) {
            payment.setPaidAt(LocalDateTime.now());
        }

        return mapToDTO(repository.save(payment));
    }

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {

        log.info("Processing payment for slotId: {}, patientId: {}, providerId: {}",
                request.getSlotId(), request.getPatientId(), request.getProviderId());

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        if (resolveMode(request) == null || resolveMode(request).isBlank()) {
            throw new BadRequestException("Payment mode is required");
        }

        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new BadRequestException("Currency is required");
        }

        Payment payment = new Payment();
        payment.setPatientId(request.getPatientId());
        payment.setProviderId(request.getProviderId());
        payment.setSlotId(request.getSlotId());
        payment.setAmount(request.getAmount());
        payment.setMode(resolveMode(request).toUpperCase());
        payment.setCurrency(request.getCurrency().toUpperCase());

        if ("CASH".equalsIgnoreCase(resolveMode(request))) {
            payment.setStatus("CASH_PENDING");

            Payment saved = repository.save(payment);

            publishNotification(saved, "PAYMENT", "Cash Payment Selected",
                    "Your appointment payment will be paid at clinic.");

            return mapToDTO(saved);
        }

        try {
            long amountInPaise = Math.round(request.getAmount() * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", request.getCurrency().toUpperCase());
            orderRequest.put("receipt", "slot_" + request.getSlotId() + "_patient_" + request.getPatientId());

            Order order = razorpayClient.orders.create(orderRequest);

            payment.setStatus("PENDING");
            payment.setRazorpayOrderId(order.get("id"));
            payment.setTransactionId(order.get("id"));

            Payment saved = repository.save(payment);

            publishNotification(saved, "PAYMENT", "Payment Initiated",
                    "Payment order created. Please complete your payment.");

            return mapToDTO(saved);

        } catch (Exception e) {
            log.error("Razorpay order creation failed", e);
            throw new BadRequestException("Unable to create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponseDTO verifyRazorpayPayment(RazorpayVerifyRequestDTO request) {
        Payment payment = repository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean valid = Utils.verifyPaymentSignature(options, razorpaySecret);

            if (!valid) {
                payment.setStatus("FAILED");
                repository.save(payment);
                throw new BadRequestException("Invalid Razorpay signature");
            }

            payment.setStatus("PAID");
            payment.setPaidAt(LocalDateTime.now());
            payment.setRazorpayOrderId(request.getRazorpayOrderId());
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setTransactionId(request.getRazorpayPaymentId());

            Payment saved = repository.save(payment);

            publishNotification(saved, "PAYMENT", "Payment Successful",
                    "Your payment has been completed successfully.");

            return mapToDTO(saved);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Payment verification failed", e);
            throw new BadRequestException("Payment verification failed");
        }
    }

    @Override
    public Optional<PaymentResponseDTO> getPaymentByAppointment(Long appointmentId) {
        return repository.findByAppointmentId(appointmentId)
                .map(this::mapToDTO);
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByPatient(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByProvider(Long providerId) {
        return repository.findByProviderId(providerId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PaymentResponseDTO> getPaymentHistory() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public PaymentResponseDTO refundPayment(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!"PAID".equalsIgnoreCase(payment.getStatus())) {
            throw new BadRequestException("Only paid payments can be refunded");
        }

        try {
            if (payment.getRazorpayPaymentId() != null) {
                razorpayClient.payments.refund(payment.getRazorpayPaymentId());
            }

            payment.setStatus("REFUNDED");
            payment.setRefundedAt(LocalDateTime.now());

            Payment saved = repository.save(payment);

            publishNotification(saved, "PAYMENT", "Payment Refunded",
                    "Your payment refund has been initiated.");

            return mapToDTO(saved);

        } catch (Exception e) {
            log.error("Refund failed", e);
            throw new BadRequestException("Refund failed");
        }
    }

    @Override
    public String getPaymentStatus(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return payment.getStatus();
    }

    @Override
    public PaymentResponseDTO updatePaymentStatus(Long id, String status) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        payment.setStatus(status.toUpperCase());

        if ("PAID".equalsIgnoreCase(status)) {
            payment.setPaidAt(LocalDateTime.now());
        }

        if ("REFUNDED".equalsIgnoreCase(status)) {
            payment.setRefundedAt(LocalDateTime.now());
        }

        Payment saved = repository.save(payment);

        publishNotification(saved, "PAYMENT", "Payment Status Updated",
                "Your payment status is now: " + saved.getStatus());

        return mapToDTO(saved);
    }

    @Override
    public String generateInvoice(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return "INVOICE\n"
                + "Invoice No: INV-" + payment.getPaymentId() + "\n"
                + "Appointment ID: " + payment.getAppointmentId() + "\n"
                + "Patient ID: " + payment.getPatientId() + "\n"
                + "Amount: " + payment.getAmount() + " " + payment.getCurrency() + "\n"
                + "Status: " + payment.getStatus() + "\n"
                + "Transaction ID: " + payment.getTransactionId() + "\n";
    }

    @Override
    public Double getTotalRevenue() {
        return repository.findAll()
                .stream()
                .filter(payment -> "PAID".equalsIgnoreCase(payment.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    private void publishNotification(Payment payment, String type, String title, String message) {
        try {
            NotificationEventDTO event = NotificationEventDTO.builder()
                    .recipientId(payment.getPatientId())
                    .type(type)
                    .title(title)
                    .message(message)
                    .channel("APP")
                    .relatedId(payment.getPaymentId())
                    .relatedType("PAYMENT")
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    event
            );

        } catch (Exception e) {
            log.error("Failed to publish notification event", e);
        }
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .appointmentId(payment.getAppointmentId())
                .patientId(payment.getPatientId())
                .providerId(payment.getProviderId())
                .slotId(payment.getSlotId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .mode(payment.getMode())
                .transactionId(payment.getTransactionId())
                .currency(payment.getCurrency())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .paidAt(payment.getPaidAt())
                .refundedAt(payment.getRefundedAt())
                .notes(payment.getNotes())
                .build();
    }

    private Optional<Payment> findExistingPaymentForRecord(PaymentRequestDTO request) {
        if (request.getAppointmentId() != null) {
            Optional<Payment> byAppointment = repository.findByAppointmentId(request.getAppointmentId());
            if (byAppointment.isPresent()) {
                return byAppointment;
            }
        }

        String transactionId = request.getTransactionId() != null
                ? request.getTransactionId()
                : request.getRazorpayPaymentId();

        if (transactionId != null && !transactionId.isBlank()) {
            return repository.findByTransactionId(transactionId);
        }

        return Optional.empty();
    }

    private String resolveMode(PaymentRequestDTO request) {
        return request.getPaymentMode() != null ? request.getPaymentMode() : request.getMode();
    }
}
