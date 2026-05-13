package com.medibook.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppointmentResponseDTO {

    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private Long slotId;
    private String serviceType;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
    private String modeOfConsultation;
}