package com.medibook.payment.service;

import java.util.List;
import java.util.Optional;

import com.medibook.payment.dto.PaymentRequestDTO;
import com.medibook.payment.dto.PaymentResponseDTO;
import com.medibook.payment.dto.RazorpayVerifyRequestDTO;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);

    PaymentResponseDTO createPaymentRecord(PaymentRequestDTO request);

    Optional<PaymentResponseDTO> getPaymentByAppointment(Long appointmentId);

    List<PaymentResponseDTO> getPaymentsByPatient(Long patientId);

    List<PaymentResponseDTO> getPaymentHistory();

    PaymentResponseDTO refundPayment(Long id);

    String getPaymentStatus(Long id);

    PaymentResponseDTO updatePaymentStatus(Long id, String status);

    String generateInvoice(Long id);

    Double getTotalRevenue();

    PaymentResponseDTO verifyRazorpayPayment(RazorpayVerifyRequestDTO request);

    List<PaymentResponseDTO> getPaymentsByProvider(Long providerId);
}
