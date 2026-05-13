package com.medibook.record.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicalReportResponseDTO {

    private Long reportId;
    private Long patientId;
    private Long providerId;
    private Long appointmentId;
    private String diagnosis;
    private String prescription;
    private String notes;
    private LocalDate reportDate;
    private String providerName;
    private LocalDateTime createdAt;
}
