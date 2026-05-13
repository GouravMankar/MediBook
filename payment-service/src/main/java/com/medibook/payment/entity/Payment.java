package com.medibook.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@Column(unique = true)
	private Long appointmentId;

	@Column(nullable = false)
	private Long patientId;

	@Column(nullable = false)
	private Long providerId;

	@Column(nullable = false)
	private Long slotId;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private String mode;

	private String transactionId;
	private String currency;

	private String razorpayOrderId;
	private String razorpayPaymentId;
	private String razorpaySignature;

	private LocalDateTime paidAt;
	private LocalDateTime refundedAt;

	private String notes;

	@PrePersist
	public void prePersist() {
		if (status == null) {
			status = "PENDING";
		}
		if (currency == null) {
			currency = "INR";
		}
	}
}