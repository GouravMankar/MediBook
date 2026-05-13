package com.medibook.record.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medibook.record.dto.MedicalRecordRequestDTO;
import com.medibook.record.dto.MedicalRecordResponseDTO;
import com.medibook.record.entity.MedicalRecord;
import com.medibook.record.exception.BadRequestException;
import com.medibook.record.exception.ResourceNotFoundException;
import com.medibook.record.repository.MedicalRecordRepository;
import com.medibook.record.service.MedicalRecordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository repository;

    @Override
    public MedicalRecordResponseDTO createRecord(MedicalRecordRequestDTO request) {
        log.info("Creating medical record for appointmentId: {}", request.getAppointmentId());

        if (repository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new BadRequestException("Medical record already exists for this appointment");
        }

        MedicalRecord record = MedicalRecord.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .diagnosis(request.getDiagnosis())
                .prescription(request.getPrescription())
                .notes(request.getNotes())
                .attachmentUrl(request.getAttachmentUrl())
                .followUpDate(request.getFollowUpDate())
                .build();

        return mapToDTO(repository.save(record));
    }

    @Override
    public Optional<MedicalRecordResponseDTO> getRecordById(Long id) {
        return repository.findById(id).map(this::mapToDTO);
    }

    @Override
    public Optional<MedicalRecordResponseDTO> getRecordByAppointment(Long appointmentId) {
        return repository.findByAppointmentId(appointmentId).map(this::mapToDTO);
    }

    @Override
    public List<MedicalRecordResponseDTO> getRecordsByPatient(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<MedicalRecordResponseDTO> getRecordsByProvider(Long providerId) {
        return repository.findByProviderId(providerId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public MedicalRecordResponseDTO updateRecord(Long id, MedicalRecordRequestDTO request) {
        MedicalRecord record = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found"));

        record.setDiagnosis(request.getDiagnosis());
        record.setPrescription(request.getPrescription());
        record.setNotes(request.getNotes());
        record.setAttachmentUrl(request.getAttachmentUrl());
        record.setFollowUpDate(request.getFollowUpDate());

        return mapToDTO(repository.save(record));
    }

    @Override
    public void deleteRecord(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Medical record not found");
        }

        repository.deleteById(id);
    }

    @Override
    public List<MedicalRecordResponseDTO> getAllRecords() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private MedicalRecordResponseDTO mapToDTO(MedicalRecord record) {
        return MedicalRecordResponseDTO.builder()
                .recordId(record.getRecordId())
                .appointmentId(record.getAppointmentId())
                .patientId(record.getPatientId())
                .providerId(record.getProviderId())
                .diagnosis(record.getDiagnosis())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                .attachmentUrl(record.getAttachmentUrl())
                .followUpDate(record.getFollowUpDate())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}