package com.medibook.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    private Long appointmentId;

    @NotNull(message = "PatientId is required")
    private Long patientId;

    @NotNull(message = "ProviderId is required")
    private Long providerId;

    private Long slotId;

    @NotNull(message = "Amount is required")
    private Double amount;

    private String paymentMode;

    private String mode;

    @NotBlank(message = "Payment currency is required")
    private String currency;

    private String status;
    private String transactionId;
    private String razorpayPaymentId;

}
