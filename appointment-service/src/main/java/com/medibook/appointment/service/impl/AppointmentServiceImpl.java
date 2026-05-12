package com.medibook.appointment.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medibook.appointment.client.ScheduleClient;
import com.medibook.appointment.dto.AppointmentRequestDTO;
import com.medibook.appointment.dto.AppointmentResponseDTO;
import com.medibook.appointment.entity.Appointment;
import com.medibook.appointment.exception.BadRequestException;
import com.medibook.appointment.exception.ResourceNotFoundException;
import com.medibook.appointment.repository.AppointmentRepository;
import com.medibook.appointment.service.AppointmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private static final String STATUS_SCHEDULED = "SCHEDULED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_RESCHEDULED = "RESCHEDULED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String APPOINTMENT_NOT_FOUND = "Appointment not found";

    private final AppointmentRepository repository;
    private final ScheduleClient scheduleClient;

    @Override
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request) {

        log.info("Booking appointment for patientId: {}, providerId: {}, slotId: {}",
                request.getPatientId(), request.getProviderId(), request.getSlotId());

        if (request.getSlotId() == null) {
            throw new BadRequestException("SlotId is required");
        }

        if (request.getAppointmentDate() == null || request.getStartTime() == null
                || LocalDateTime.of(request.getAppointmentDate(), request.getStartTime()).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Past appointment slots cannot be booked");
        }

        if (repository.existsBySlotIdAndStatusIn(
                request.getSlotId(),
                List.of(STATUS_SCHEDULED, STATUS_RESCHEDULED))) {
            throw new BadRequestException("Appointment already exists for this slot");
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setProviderId(request.getProviderId());
        appointment.setSlotId(request.getSlotId());
        appointment.setServiceType(request.getServiceType());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setNotes(request.getNotes());
        appointment.setModeOfConsultation(request.getModeOfConsultation());
        appointment.setStatus(STATUS_SCHEDULED);

        try {
            scheduleClient.bookSlot(request.getSlotId());

            Appointment saved = repository.save(appointment);

            log.info("Appointment booked successfully with id: {}", saved.getAppointmentId());

            return mapToDTO(saved);

        } catch (Exception e) {
            log.error("Appointment booking failed. Rolling back slot booking for slotId: {}",
                    request.getSlotId(), e);

            try {
                scheduleClient.unblockSlot(request.getSlotId());
            } catch (Exception rollbackException) {
                log.error("Slot rollback failed for slotId: {}", request.getSlotId(), rollbackException);
            }

            throw new BadRequestException("Appointment booking failed");
        }
    }

    @Override
    public Optional<AppointmentResponseDTO> getById(Long id) {
        return repository.findById(id).map(this::mapToDTO);
    }

    @Override
    public List<AppointmentResponseDTO> getByPatient(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<AppointmentResponseDTO> getByProvider(Long providerId) {
        return repository.findByProviderId(providerId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<AppointmentResponseDTO> getByProviderAndDate(Long providerId, LocalDate date) {
        return repository.findByProviderIdAndAppointmentDate(providerId, date)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public AppointmentResponseDTO cancelAppointment(Long id) {

        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(APPOINTMENT_NOT_FOUND));

        if (STATUS_CANCELLED.equalsIgnoreCase(appointment.getStatus())) {
            throw new BadRequestException("Appointment is already cancelled");
        }

        appointment.setStatus(STATUS_CANCELLED);
        Appointment saved = repository.save(appointment);

        if (appointment.getSlotId() != null) {
            try {
                scheduleClient.unblockSlot(appointment.getSlotId());
            } catch (Exception e) {
                log.error("Failed to release slot for cancelled appointmentId: {}, slotId: {}",
                        id, appointment.getSlotId(), e);
                throw new BadRequestException("Appointment cancelled but slot could not be released");
            }
        }

        log.info("Appointment cancelled successfully. appointmentId: {}, slotId: {}",
                id, appointment.getSlotId());
        return mapToDTO(saved);
    }

    @Override
    public AppointmentResponseDTO rescheduleAppointment(Long id, AppointmentRequestDTO request) {

        Appointment existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(APPOINTMENT_NOT_FOUND));

        if (STATUS_CANCELLED.equalsIgnoreCase(existing.getStatus())) {
            throw new BadRequestException("Cancelled appointment cannot be rescheduled");
        }

        if (request.getSlotId() == null) {
            throw new BadRequestException("SlotId is required");
        }

        if (request.getAppointmentDate() == null || request.getStartTime() == null
                || LocalDateTime.of(request.getAppointmentDate(), request.getStartTime()).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Past appointment slots cannot be rescheduled");
        }

        if (repository.existsBySlotIdAndStatusIn(
                request.getSlotId(),
                List.of(STATUS_SCHEDULED, STATUS_RESCHEDULED))) {
            throw new BadRequestException("Appointment already exists for this new slot");
        }

        Long oldSlotId = existing.getSlotId();

        try {
            if (oldSlotId != null) {
                scheduleClient.unblockSlot(oldSlotId);
            }

            scheduleClient.bookSlot(request.getSlotId());

            existing.setSlotId(request.getSlotId());
            existing.setProviderId(request.getProviderId());
            existing.setPatientId(request.getPatientId());
            existing.setAppointmentDate(request.getAppointmentDate());
            existing.setStartTime(request.getStartTime());
            existing.setEndTime(request.getEndTime());
            existing.setServiceType(request.getServiceType());
            existing.setNotes(request.getNotes());
            existing.setModeOfConsultation(request.getModeOfConsultation());
            existing.setStatus(STATUS_RESCHEDULED);

            Appointment saved = repository.save(existing);

            log.info("Appointment rescheduled: {}", id);

            return mapToDTO(saved);

        } catch (Exception e) {
            log.error("Appointment reschedule failed for appointmentId: {}", id, e);

            try {
                scheduleClient.unblockSlot(request.getSlotId());

                if (oldSlotId != null) {
                    scheduleClient.bookSlot(oldSlotId);
                }
            } catch (Exception rollbackException) {
                log.error("Reschedule rollback failed for appointmentId: {}", id, rollbackException);
            }

            throw new BadRequestException("Appointment reschedule failed");
        }
    }

    @Override
    public AppointmentResponseDTO completeAppointment(Long id) {

        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(APPOINTMENT_NOT_FOUND));

        if (STATUS_CANCELLED.equalsIgnoreCase(appointment.getStatus())) {
            throw new BadRequestException("Cancelled appointment cannot be completed");
        }

        appointment.setStatus(STATUS_COMPLETED);
        Appointment saved = repository.save(appointment);

        log.info("Appointment completed: {}", id);
        return mapToDTO(saved);
    }

    @Override
    public AppointmentResponseDTO updateStatus(Long id, String status) {

        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(APPOINTMENT_NOT_FOUND));

        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status is required");
        }

        appointment.setStatus(status.toUpperCase());
        Appointment saved = repository.save(appointment);

        log.info("Appointment status updated: {} -> {}", id, status);
        return mapToDTO(saved);
    }

    @Override
    public List<AppointmentResponseDTO> getUpcomingByPatient(Long patientId) {
        return repository
                .findUpcomingByPatient(patientId, LocalDate.now(), STATUS_SCHEDULED)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Long getAppointmentCount(Long providerId) {
        return repository.countByProviderId(providerId);
    }

    private AppointmentResponseDTO mapToDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatientId())
                .providerId(appointment.getProviderId())
                .slotId(appointment.getSlotId())
                .serviceType(appointment.getServiceType())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .modeOfConsultation(appointment.getModeOfConsultation())
                .build();
    }
}
