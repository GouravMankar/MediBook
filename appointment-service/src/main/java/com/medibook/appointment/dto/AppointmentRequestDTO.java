package com.medibook.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentRequestDTO {

    @NotNull(message = "PatientId is required")
    private Long patientId;

    @NotNull(message = "ProviderId is required")
    private Long providerId;

    @NotNull(message = "SlotId is required")
    private Long slotId;

    private String serviceType;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    private String notes;

    @NotBlank(message = "Mode of consultation is required")
    private String modeOfConsultation;
}