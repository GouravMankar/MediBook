package com.medibook.appointment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medibook.appointment.client.ScheduleClient;
import com.medibook.appointment.dto.AppointmentRequestDTO;
import com.medibook.appointment.dto.AppointmentResponseDTO;
import com.medibook.appointment.entity.Appointment;
import com.medibook.appointment.exception.BadRequestException;
import com.medibook.appointment.exception.ResourceNotFoundException;
import com.medibook.appointment.repository.AppointmentRepository;
import com.medibook.appointment.service.impl.AppointmentServiceImpl;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private ScheduleClient scheduleClient;

    @InjectMocks
    private AppointmentServiceImpl service;

    @Test
    void cancelAppointmentUpdatesStatusAndReleasesSlot() {
        Appointment appointment = appointment("SCHEDULED");
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponseDTO response = service.cancelAppointment(1L);

        assertThat(response.getStatus()).isEqualTo("CANCELLED");
        verify(scheduleClient).unblockSlot(10L);
    }

    @Test
    void bookAppointmentCreatesScheduledAppointmentAndBooksSlot() {
        AppointmentRequestDTO request = request(22L);
        when(repository.existsBySlotIdAndStatusIn(any(), any())).thenReturn(false);
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setAppointmentId(99L);
            return appointment;
        });

        AppointmentResponseDTO response = service.bookAppointment(request);

        assertThat(response.getAppointmentId()).isEqualTo(99L);
        assertThat(response.getStatus()).isEqualTo("SCHEDULED");
        verify(scheduleClient).bookSlot(22L);
    }

    @Test
    void bookAppointmentRejectsMissingSlotAndDuplicateSlot() {
        AppointmentRequestDTO missingSlot = request(null);

        assertThatThrownBy(() -> service.bookAppointment(missingSlot))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("SlotId");

        AppointmentRequestDTO duplicate = request(44L);
        when(repository.existsBySlotIdAndStatusIn(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.bookAppointment(duplicate))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void bookAppointmentRejectsPastDate() {
        AppointmentRequestDTO past = request(22L);
        past.setAppointmentDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> service.bookAppointment(past))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Past appointment");
    }

    @Test
    void rescheduleAppointmentMovesSlotsAndUpdatesDetails() {
        Appointment existing = appointment("SCHEDULED");
        AppointmentRequestDTO request = request(55L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsBySlotIdAndStatusIn(any(), any())).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);

        AppointmentResponseDTO response = service.rescheduleAppointment(1L, request);

        assertThat(response.getSlotId()).isEqualTo(55L);
        assertThat(response.getStatus()).isEqualTo("RESCHEDULED");
        verify(scheduleClient).unblockSlot(10L);
        verify(scheduleClient).bookSlot(55L);
    }

    @Test
    void cancelCompleteAndRescheduleRejectInvalidStates() {
        Appointment cancelled = appointment("CANCELLED");
        when(repository.findById(1L)).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> service.cancelAppointment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already cancelled");

        assertThatThrownBy(() -> service.completeAppointment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot be completed");

        assertThatThrownBy(() -> service.rescheduleAppointment(1L, request(22L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot be rescheduled");
    }

    @Test
    void mutationsRequireExistingAppointment() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelAppointment(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.completeAppointment(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.updateStatus(99L, "COMPLETED"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void rescheduleRejectsMissingPastAndDuplicateSlot() {
        Appointment existing = appointment("SCHEDULED");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.rescheduleAppointment(1L, request(null)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("SlotId");

        AppointmentRequestDTO past = request(22L);
        past.setAppointmentDate(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> service.rescheduleAppointment(1L, past))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Past appointment");

        when(repository.existsBySlotIdAndStatusIn(any(), any())).thenReturn(true);
        assertThatThrownBy(() -> service.rescheduleAppointment(1L, request(22L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void bookingFailureRollsBackSlotAndRescheduleFailureRestoresOldSlot() {
        AppointmentRequestDTO request = request(22L);
        when(repository.existsBySlotIdAndStatusIn(any(), any())).thenReturn(false);
        when(repository.save(any(Appointment.class))).thenThrow(new RuntimeException("db"));

        assertThatThrownBy(() -> service.bookAppointment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("booking failed");
        verify(scheduleClient).unblockSlot(22L);

        Appointment existing = appointment("SCHEDULED");
        AppointmentRequestDTO reschedule = request(33L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        doThrow(new RuntimeException("schedule")).when(scheduleClient).bookSlot(33L);

        assertThatThrownBy(() -> service.rescheduleAppointment(1L, reschedule))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("reschedule failed");
        verify(scheduleClient).bookSlot(10L);
    }

    @Test
    void updateStatusRejectsBlankAndPersistsUppercaseStatus() {
        Appointment appointment = appointment("SCHEDULED");
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.updateStatus(1L, " "))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Status");

        when(repository.save(appointment)).thenReturn(appointment);
        AppointmentResponseDTO response = service.updateStatus(1L, "no_show");

        assertThat(response.getStatus()).isEqualTo("NO_SHOW");
    }

    @Test
    void cancelAppointmentFailsWhenSlotCannotBeReleased() {
        Appointment appointment = appointment("SCHEDULED");
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("schedule down")).when(scheduleClient).unblockSlot(10L);

        assertThatThrownBy(() -> service.cancelAppointment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("slot could not be released");
    }

    @Test
    void retrievalMethodsMapRepositoryResults() {
        Appointment appointment = appointment("SCHEDULED");
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(repository.findByPatientId(2L)).thenReturn(List.of(appointment));
        when(repository.findByProviderId(3L)).thenReturn(List.of(appointment));
        when(repository.findByProviderIdAndAppointmentDate(3L, appointment.getAppointmentDate()))
                .thenReturn(List.of(appointment));
        when(repository.countByProviderId(3L)).thenReturn(7L);

        assertThat(service.getById(1L)).isPresent();
        assertThat(service.getByPatient(2L)).hasSize(1);
        assertThat(service.getByProvider(3L)).hasSize(1);
        assertThat(service.getByProviderAndDate(3L, appointment.getAppointmentDate())).hasSize(1);
        assertThat(service.getAppointmentCount(3L)).isEqualTo(7L);
    }

    @Test
    void completeAppointmentReturnsUpdatedStatus() {
        Appointment appointment = appointment("SCHEDULED");
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponseDTO response = service.completeAppointment(1L);

        assertThat(response.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void upcomingPatientAppointmentsUseScheduledStatusOnly() {
        Appointment appointment = appointment("SCHEDULED");
        when(repository.findUpcomingByPatient(2L, LocalDate.now(), "SCHEDULED"))
                .thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> response = service.getUpcomingByPatient(2L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getStatus()).isEqualTo("SCHEDULED");
    }

    private Appointment appointment(String status) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setPatientId(2L);
        appointment.setProviderId(3L);
        appointment.setSlotId(10L);
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setStartTime(LocalTime.of(10, 0));
        appointment.setEndTime(LocalTime.of(10, 30));
        appointment.setModeOfConsultation("IN_PERSON");
        appointment.setStatus(status);
        return appointment;
    }

    private AppointmentRequestDTO request(Long slotId) {
        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setPatientId(2L);
        request.setProviderId(3L);
        request.setSlotId(slotId);
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(10, 30));
        request.setServiceType("GENERAL");
        request.setModeOfConsultation("IN_PERSON");
        return request;
    }
}
