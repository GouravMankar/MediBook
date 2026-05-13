package com.medibook.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medibook.record.dto.MedicalRecordRequestDTO;
import com.medibook.record.dto.MedicalRecordResponseDTO;
import com.medibook.record.entity.MedicalRecord;
import com.medibook.record.exception.BadRequestException;
import com.medibook.record.exception.ResourceNotFoundException;
import com.medibook.record.repository.MedicalRecordRepository;
import com.medibook.record.service.impl.MedicalRecordServiceImpl;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceImplTest {

    @Mock
    private MedicalRecordRepository repository;

    @InjectMocks
    private MedicalRecordServiceImpl service;

    @Test
    void createRecordStoresNewRecord() {
        MedicalRecordRequestDTO request = request();
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.empty());
        when(repository.save(any(MedicalRecord.class))).thenAnswer(invocation -> {
            MedicalRecord record = invocation.getArgument(0);
            record.setRecordId(50L);
            return record;
        });

        MedicalRecordResponseDTO response = service.createRecord(request);

        assertThat(response.getRecordId()).isEqualTo(50L);
        assertThat(response.getDiagnosis()).isEqualTo("Flu");
    }

    @Test
    void createRecordRejectsDuplicateAppointmentRecord() {
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.of(record()));

        assertThatThrownBy(() -> service.createRecord(request()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void updateRecordUpdatesEditableFields() {
        MedicalRecord existing = record();
        MedicalRecordRequestDTO request = request();
        request.setDiagnosis("Migraine");
        request.setPrescription("Hydration");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        MedicalRecordResponseDTO response = service.updateRecord(1L, request);

        assertThat(response.getDiagnosis()).isEqualTo("Migraine");
        assertThat(response.getPrescription()).isEqualTo("Hydration");
    }

    @Test
    void deleteRecordRequiresExistingRecord() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteRecord(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        when(repository.existsById(2L)).thenReturn(true);
        service.deleteRecord(2L);
        verify(repository).deleteById(2L);
    }

    @Test
    void retrievalMethodsMapRepositoryResults() {
        MedicalRecord record = record();
        when(repository.findById(1L)).thenReturn(Optional.of(record));
        when(repository.findByAppointmentId(7L)).thenReturn(Optional.of(record));
        when(repository.findByPatientId(2L)).thenReturn(List.of(record));
        when(repository.findByProviderId(3L)).thenReturn(List.of(record));
        when(repository.findAll()).thenReturn(List.of(record));

        assertThat(service.getRecordById(1L)).isPresent();
        assertThat(service.getRecordByAppointment(7L)).isPresent();
        assertThat(service.getRecordsByPatient(2L)).hasSize(1);
        assertThat(service.getRecordsByProvider(3L)).hasSize(1);
        assertThat(service.getAllRecords()).hasSize(1);
    }

    private MedicalRecordRequestDTO request() {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO();
        request.setAppointmentId(7L);
        request.setPatientId(2L);
        request.setProviderId(3L);
        request.setDiagnosis("Flu");
        request.setPrescription("Rest");
        request.setNotes("Drink fluids");
        request.setAttachmentUrl("file.pdf");
        request.setFollowUpDate(LocalDate.now().plusDays(7));
        return request;
    }

    private MedicalRecord record() {
        return MedicalRecord.builder()
                .recordId(1L)
                .appointmentId(7L)
                .patientId(2L)
                .providerId(3L)
                .diagnosis("Flu")
                .prescription("Rest")
                .notes("Drink fluids")
                .attachmentUrl("file.pdf")
                .followUpDate(LocalDate.now().plusDays(7))
                .build();
    }
}
