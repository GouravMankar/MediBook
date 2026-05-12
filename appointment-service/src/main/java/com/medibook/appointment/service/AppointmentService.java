package com.medibook.appointment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.medibook.appointment.dto.AppointmentRequestDTO;
import com.medibook.appointment.dto.AppointmentResponseDTO;

public interface AppointmentService {

    AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request);

    Optional<AppointmentResponseDTO> getById(Long id);

    List<AppointmentResponseDTO> getByPatient(Long patientId);

    List<AppointmentResponseDTO> getByProvider(Long providerId);

    List<AppointmentResponseDTO> getByProviderAndDate(Long providerId, LocalDate date);

    AppointmentResponseDTO cancelAppointment(Long id);

    AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRequestDTO request);

    AppointmentResponseDTO completeAppointment(Long id);

    AppointmentResponseDTO updateStatus(Long id, String status);

    List<AppointmentResponseDTO> getUpcomingByPatient(Long patientId);

    Long getAppointmentCount(Long providerId);
}
