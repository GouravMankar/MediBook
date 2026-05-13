package com.medibook.payment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDTO {

    private Long paymentId;
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private Long slotId;
    private Double amount;
    private String status;
    private String mode;
    private String transactionId;
    private String currency;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private String notes;
}
