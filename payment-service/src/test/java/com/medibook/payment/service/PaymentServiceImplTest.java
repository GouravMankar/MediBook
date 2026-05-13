package com.medibook.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.medibook.payment.dto.PaymentRequestDTO;
import com.medibook.payment.dto.PaymentResponseDTO;
import com.medibook.payment.dto.RazorpayVerifyRequestDTO;
import com.medibook.payment.entity.Payment;
import com.medibook.payment.exception.BadRequestException;
import com.medibook.payment.exception.ResourceNotFoundException;
import com.medibook.payment.repository.PaymentRepository;
import com.medibook.payment.service.impl.PaymentServiceImpl;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.OrderClient;
import com.razorpay.PaymentClient;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentServiceImpl service;

    @Test
    void updatePaymentStatusToRefundedSetsRefundedAt() {
        Payment payment = payment(1L, "PAID");
        when(repository.findById(1L)).thenReturn(Optional.of(payment));
        when(repository.save(payment)).thenReturn(payment);

        PaymentResponseDTO response = service.updatePaymentStatus(1L, "REFUNDED");

        assertThat(response.getStatus()).isEqualTo("REFUNDED");
        assertThat(response.getRefundedAt()).isNotNull();
        verify(repository).save(payment);
    }

    @Test
    void updatePaymentStatusToPaidSetsPaidAt() {
        Payment payment = payment(1L, "PENDING");
        when(repository.findById(1L)).thenReturn(Optional.of(payment));
        when(repository.save(payment)).thenReturn(payment);

        PaymentResponseDTO response = service.updatePaymentStatus(1L, "PAID");

        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getPaidAt()).isNotNull();
    }

    @Test
    void processCashPaymentStoresCashPendingAndPublishesNotification() {
        PaymentRequestDTO request = request();
        request.setMode("CASH");
        when(repository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(8L);
            return payment;
        });

        PaymentResponseDTO response = service.processPayment(request);

        assertThat(response.getStatus()).isEqualTo("CASH_PENDING");
        assertThat(response.getMode()).isEqualTo("CASH");
        verify(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));
    }

    @Test
    void processOnlinePaymentCreatesRazorpayOrderAndPendingPayment() throws Exception {
        OrderClient orderClient = mock(OrderClient.class);
        razorpayClient.orders = orderClient;
        when(orderClient.create(any(org.json.JSONObject.class)))
                .thenReturn(new Order(new org.json.JSONObject().put("id", "order_123")));
        when(repository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(9L);
            return payment;
        });

        PaymentResponseDTO response = service.processPayment(request());

        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getRazorpayOrderId()).isEqualTo("order_123");
        assertThat(response.getTransactionId()).isEqualTo("order_123");
    }

    @Test
    void createPaymentRecordCanReuseTransactionPaymentAndDefaultsCurrencyStatus() {
        Payment existing = payment(5L, "PENDING");
        PaymentRequestDTO request = request();
        request.setAppointmentId(88L);
        request.setTransactionId("txn_1");
        request.setCurrency(null);
        request.setStatus(null);
        when(repository.findByAppointmentId(88L)).thenReturn(Optional.empty());
        when(repository.findByTransactionId("txn_1")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        PaymentResponseDTO response = service.createPaymentRecord(request);

        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getCurrency()).isEqualTo("INR");
    }

    @Test
    void createPaymentRecordRejectsMissingAppointmentAndUsesRazorpayTransaction() {
        PaymentRequestDTO missingAppointment = request();
        missingAppointment.setAppointmentId(null);

        assertThatThrownBy(() -> service.createPaymentRecord(missingAppointment))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("AppointmentId");

        PaymentRequestDTO request = request();
        request.setTransactionId(null);
        request.setRazorpayPaymentId("pay_123");
        request.setPaymentMode("card");
        when(repository.findByAppointmentId(77L)).thenReturn(Optional.empty());
        when(repository.findByTransactionId("pay_123")).thenReturn(Optional.empty());
        when(repository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(12L);
            return payment;
        });

        PaymentResponseDTO response = service.createPaymentRecord(request);

        assertThat(response.getTransactionId()).isEqualTo("pay_123");
        assertThat(response.getRazorpayPaymentId()).isEqualTo("pay_123");
        assertThat(response.getMode()).isEqualTo("CARD");
    }

    @Test
    void processPaymentRejectsInvalidAmountAndMissingCurrency() {
        PaymentRequestDTO request = request();
        request.setAmount(0.0);

        assertThatThrownBy(() -> service.processPayment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Amount");

        request.setAmount(700.0);
        request.setCurrency("");

        assertThatThrownBy(() -> service.processPayment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Currency");
    }

    @Test
    void processPaymentRejectsMissingModeAndWrapsRazorpayFailure() throws Exception {
        PaymentRequestDTO missingMode = request();
        missingMode.setMode(null);
        missingMode.setPaymentMode(null);

        assertThatThrownBy(() -> service.processPayment(missingMode))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Payment mode");

        OrderClient orderClient = mock(OrderClient.class);
        razorpayClient.orders = orderClient;
        when(orderClient.create(any(org.json.JSONObject.class))).thenThrow(new RuntimeException("gateway down"));

        assertThatThrownBy(() -> service.processPayment(request()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unable to create Razorpay order");
    }

    @Test
    void verifyRazorpayPaymentRejectsMissingPayment() {
        RazorpayVerifyRequestDTO request = new RazorpayVerifyRequestDTO();
        request.setPaymentId(99L);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verifyRazorpayPayment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void refundPaymentUpdatesPaidPaymentAndRejectsNonPaidPayment() {
        Payment paid = payment(1L, "PAID");
        when(repository.findById(1L)).thenReturn(Optional.of(paid));
        when(repository.save(paid)).thenReturn(paid);

        PaymentResponseDTO refunded = service.refundPayment(1L);

        assertThat(refunded.getStatus()).isEqualTo("REFUNDED");
        assertThat(refunded.getRefundedAt()).isNotNull();

        when(repository.findById(2L)).thenReturn(Optional.of(payment(2L, "PENDING")));

        assertThatThrownBy(() -> service.refundPayment(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only paid");
    }

    @Test
    void refundPaymentCallsRazorpayWhenPaymentIdExistsAndWrapsRefundFailure() throws Exception {
        PaymentClient paymentClient = mock(PaymentClient.class);
        razorpayClient.payments = paymentClient;
        Payment paid = payment(1L, "PAID");
        paid.setRazorpayPaymentId("pay_123");
        when(repository.findById(1L)).thenReturn(Optional.of(paid));
        when(repository.save(paid)).thenReturn(paid);

        PaymentResponseDTO response = service.refundPayment(1L);

        assertThat(response.getStatus()).isEqualTo("REFUNDED");
        verify(paymentClient).refund("pay_123");

        Payment failing = payment(2L, "PAID");
        failing.setRazorpayPaymentId("pay_fail");
        when(repository.findById(2L)).thenReturn(Optional.of(failing));
        doThrow(new RuntimeException("refund down")).when(paymentClient).refund("pay_fail");

        assertThatThrownBy(() -> service.refundPayment(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Refund failed");
    }

    @Test
    void queryAndInvoiceMethodsMapRepositoryData() {
        Payment payment = payment(4L, "PAID");
        when(repository.findByAppointmentId(104L)).thenReturn(Optional.of(payment));
        when(repository.findByPatientId(1L)).thenReturn(List.of(payment));
        when(repository.findByProviderId(2L)).thenReturn(List.of(payment));
        when(repository.findAll()).thenReturn(List.of(payment));
        when(repository.findById(4L)).thenReturn(Optional.of(payment));

        assertThat(service.getPaymentByAppointment(104L)).isPresent();
        assertThat(service.getPaymentsByPatient(1L)).hasSize(1);
        assertThat(service.getPaymentsByProvider(2L)).hasSize(1);
        assertThat(service.getPaymentHistory()).hasSize(1);
        assertThat(service.getPaymentStatus(4L)).isEqualTo("PAID");
        assertThat(service.generateInvoice(4L)).contains("INV-4", "Appointment ID: 104");
    }

    @Test
    void missingPaymentLookupsThrowResourceNotFound() {
        when(repository.findById(91L)).thenReturn(Optional.empty());
        when(repository.findById(92L)).thenReturn(Optional.empty());
        when(repository.findById(93L)).thenReturn(Optional.empty());
        when(repository.findById(94L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPaymentStatus(91L))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.updatePaymentStatus(92L, "PAID"))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.generateInvoice(93L))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.refundPayment(94L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPaymentRecordReusesExistingAppointmentPayment() {
        Payment existing = payment(3L, "PENDING");
        existing.setAppointmentId(77L);
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setAppointmentId(77L);
        request.setPatientId(11L);
        request.setProviderId(12L);
        request.setSlotId(13L);
        request.setAmount(700.0);
        request.setPaymentMode("UPI");
        request.setCurrency("INR");
        request.setStatus("PAID");

        when(repository.findByAppointmentId(77L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        PaymentResponseDTO response = service.createPaymentRecord(request);

        assertThat(response.getPaymentId()).isEqualTo(3L);
        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getSlotId()).isEqualTo(13L);
    }

    @Test
    void getTotalRevenueSumsOnlyPaidPayments() {
        when(repository.findAll()).thenReturn(List.of(
                payment(1L, "PAID"),
                payment(2L, "REFUNDED"),
                payment(3L, "PAID")));

        assertThat(service.getTotalRevenue()).isEqualTo(1400.0);
    }

    private Payment payment(Long id, String status) {
        return Payment.builder()
                .paymentId(id)
                .appointmentId(id + 100)
                .patientId(1L)
                .providerId(2L)
                .slotId(3L)
                .amount(700.0)
                .mode("UPI")
                .currency("INR")
                .status(status)
                .build();
    }

    private PaymentRequestDTO request() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setAppointmentId(77L);
        request.setPatientId(1L);
        request.setProviderId(2L);
        request.setSlotId(3L);
        request.setAmount(700.0);
        request.setMode("UPI");
        request.setCurrency("INR");
        return request;
    }
}
