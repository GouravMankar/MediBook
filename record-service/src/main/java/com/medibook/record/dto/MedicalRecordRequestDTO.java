package com.medibook.record.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicalRecordRequestDTO {

    @NotNull
    private Long appointmentId;

    @NotNull
    private Long patientId;

    @NotNull
    private Long providerId;

    private String diagnosis;
    private String prescription;
    private String notes;
    private String attachmentUrl;
    private LocalDate followUpDate;
}