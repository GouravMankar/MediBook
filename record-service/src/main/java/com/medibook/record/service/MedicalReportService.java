package com.medibook.record.service;

import java.util.List;
import java.util.Optional;

import com.medibook.record.dto.MedicalReportRequestDTO;
import com.medibook.record.dto.MedicalReportResponseDTO;

public interface MedicalReportService {

    MedicalReportResponseDTO createReport(MedicalReportRequestDTO request);

    Optional<MedicalReportResponseDTO> getReportById(Long id);

    List<MedicalReportResponseDTO> getReportsByPatient(Long patientId);

    List<MedicalReportResponseDTO> getReportsByProvider(Long providerId);
}
