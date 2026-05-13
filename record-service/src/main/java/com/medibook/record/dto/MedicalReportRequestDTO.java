package com.medibook.record.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicalReportRequestDTO {

    @NotNull(message = "Patient is required")
    private Long patientId;

    @NotNull(message = "Provider is required")
    private Long providerId;

    @NotNull(message = "Appointment is required")
    private Long appointmentId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    @NotBlank(message = "Prescription is required")
    private String prescription;

    private String notes;
    private LocalDate reportDate;
    private String providerName;
}
