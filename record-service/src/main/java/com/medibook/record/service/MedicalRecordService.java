package com.medibook.record.service;

import java.util.List;
import java.util.Optional;

import com.medibook.record.dto.MedicalRecordRequestDTO;
import com.medibook.record.dto.MedicalRecordResponseDTO;

public interface MedicalRecordService {

    MedicalRecordResponseDTO createRecord(MedicalRecordRequestDTO request);

    Optional<MedicalRecordResponseDTO> getRecordById(Long id);

    Optional<MedicalRecordResponseDTO> getRecordByAppointment(Long appointmentId);

    List<MedicalRecordResponseDTO> getRecordsByPatient(Long patientId);

    List<MedicalRecordResponseDTO> getRecordsByProvider(Long providerId);

    MedicalRecordResponseDTO updateRecord(Long id, MedicalRecordRequestDTO request);

    void deleteRecord(Long id);

    List<MedicalRecordResponseDTO> getAllRecords();
}