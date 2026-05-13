package com.medibook.record.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_reports")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long providerId;

    @Column(nullable = false, unique = true)
    private Long appointmentId;

    @Column(nullable = false)
    private String diagnosis;

    @Column(nullable = false, length = 1000)
    private String prescription;

    @Column(length = 1500)
    private String notes;

    private LocalDate reportDate;

    private String providerName;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (reportDate == null) {
            reportDate = LocalDate.now();
        }
    }
}
