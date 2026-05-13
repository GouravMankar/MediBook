package com.medibook.record.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicalRecordResponseDTO {

    private Long recordId;
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private String diagnosis;
    private String prescription;
    private String notes;
    private String attachmentUrl;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}