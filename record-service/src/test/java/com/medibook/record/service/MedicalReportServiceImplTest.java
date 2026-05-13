package com.medibook.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medibook.record.client.AppointmentClient;
import com.medibook.record.dto.AppointmentResponseDTO;
import com.medibook.record.dto.MedicalReportRequestDTO;
import com.medibook.record.dto.MedicalReportResponseDTO;
import com.medibook.record.entity.MedicalReport;
import com.medibook.record.exception.BadRequestException;
import com.medibook.record.repository.MedicalReportRepository;
import com.medibook.record.service.impl.MedicalReportServiceImpl;

@ExtendWith(MockitoExtension.class)
class MedicalReportServiceImplTest {

    @Mock
    private MedicalReportRepository repository;

    @Mock
    private AppointmentClient appointmentClient;

    @InjectMocks
    private MedicalReportServiceImpl service;

    @Test
    void createReportRequiresMatchingCompletedAppointment() {
        MedicalReportRequestDTO request = request();
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.empty());
        when(appointmentClient.getAppointment(7L)).thenReturn(appointment("COMPLETED"));
        when(repository.save(any(MedicalReport.class))).thenAnswer(invocation -> {
            MedicalReport report = invocation.getArgument(0);
            report.setReportId(99L);
            return report;
        });

        MedicalReportResponseDTO response = service.createReport(request);

        assertThat(response.getReportId()).isEqualTo(99L);
        assertThat(response.getPatientId()).isEqualTo(2L);
        assertThat(response.getProviderId()).isEqualTo(3L);
    }

    @Test
    void createReportRejectsDuplicateAppointmentReport() {
        MedicalReportRequestDTO request = request();
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.of(MedicalReport.builder().reportId(1L).build()));

        assertThatThrownBy(() -> service.createReport(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createReportRejectsMissingAppointment() {
        MedicalReportRequestDTO request = request();
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.empty());
        when(appointmentClient.getAppointment(7L)).thenReturn(null);

        assertThatThrownBy(() -> service.createReport(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Appointment not found");
    }

    @Test
    void createReportRejectsMismatchedProvider() {
        MedicalReportRequestDTO request = request();
        request.setProviderId(4L);
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.empty());
        when(appointmentClient.getAppointment(7L)).thenReturn(appointment("COMPLETED"));

        assertThatThrownBy(() -> service.createReport(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("must match");
    }

    @Test
    void createReportRejectsScheduledAppointment() {
        MedicalReportRequestDTO request = request();
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.empty());
        when(appointmentClient.getAppointment(7L)).thenReturn(appointment("SCHEDULED"));

        assertThatThrownBy(() -> service.createReport(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("completed");
    }

    @Test
    void patientRetrievalReturnsOnlyPatientReportsFromRepository() {
        MedicalReport report = MedicalReport.builder()
                .reportId(1L)
                .appointmentId(7L)
                .patientId(2L)
                .providerId(3L)
                .diagnosis("Flu")
                .prescription("Rest")
                .build();
        when(repository.findByPatientId(2L)).thenReturn(List.of(report));

        List<MedicalReportResponseDTO> reports = service.getReportsByPatient(2L);

        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getDiagnosis()).isEqualTo("Flu");
    }

    @Test
    void idAndProviderRetrievalMapRepositoryResults() {
        MedicalReport report = MedicalReport.builder()
                .reportId(1L)
                .appointmentId(7L)
                .patientId(2L)
                .providerId(3L)
                .diagnosis("Flu")
                .prescription("Rest")
                .providerName("Dr Rao")
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(report));
        when(repository.findByProviderId(3L)).thenReturn(List.of(report));

        assertThat(service.getReportById(1L)).isPresent();
        assertThat(service.getReportById(1L).get().getProviderName()).isEqualTo("Dr Rao");
        assertThat(service.getReportsByProvider(3L)).hasSize(1);
    }

    private MedicalReportRequestDTO request() {
        MedicalReportRequestDTO request = new MedicalReportRequestDTO();
        request.setAppointmentId(7L);
        request.setPatientId(2L);
        request.setProviderId(3L);
        request.setDiagnosis("Flu");
        request.setPrescription("Rest");
        return request;
    }

    private AppointmentResponseDTO appointment(String status) {
        AppointmentResponseDTO appointment = new AppointmentResponseDTO();
        appointment.setAppointmentId(7L);
        appointment.setPatientId(2L);
        appointment.setProviderId(3L);
        appointment.setStatus(status);
        return appointment;
    }
}
