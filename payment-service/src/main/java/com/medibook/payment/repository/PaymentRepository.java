package com.medibook.payment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medibook.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAppointmentId(Long appointmentId);

    List<Payment> findByPatientId(Long patientId);

    List<Payment> findByStatus(String status);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByProviderId(Long providerId);

    List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);
}