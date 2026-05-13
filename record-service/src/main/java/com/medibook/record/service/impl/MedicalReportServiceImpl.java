package com.medibook.record.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medibook.record.client.AppointmentClient;
import com.medibook.record.dto.AppointmentResponseDTO;
import com.medibook.record.dto.MedicalReportRequestDTO;
import com.medibook.record.dto.MedicalReportResponseDTO;
import com.medibook.record.entity.MedicalReport;
import com.medibook.record.exception.BadRequestException;
import com.medibook.record.repository.MedicalReportRepository;
import com.medibook.record.service.MedicalReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicalReportServiceImpl implements MedicalReportService {

    private final MedicalReportRepository repository;
    private final AppointmentClient appointmentClient;

    @Override
    public MedicalReportResponseDTO createReport(MedicalReportRequestDTO request) {
        if (repository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new BadRequestException("Medical report already exists for this appointment");
        }

        AppointmentResponseDTO appointment = appointmentClient.getAppointment(request.getAppointmentId());

        if (appointment == null) {
            throw new BadRequestException("Appointment not found");
        }

        if (!request.getPatientId().equals(appointment.getPatientId())
                || !request.getProviderId().equals(appointment.getProviderId())) {
            throw new BadRequestException("Report patient/provider must match the appointment");
        }

        if (!"COMPLETED".equalsIgnoreCase(appointment.getStatus())) {
            throw new BadRequestException("Reports can be created only for completed appointments");
        }

        MedicalReport report = MedicalReport.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .providerName(request.getProviderName())
                .diagnosis(request.getDiagnosis())
                .prescription(request.getPrescription())
                .notes(request.getNotes())
                .reportDate(request.getReportDate())
                .build();

        return mapToDTO(repository.save(report));
    }

    @Override
    public Optional<MedicalReportResponseDTO> getReportById(Long id) {
        return repository.findById(id).map(this::mapToDTO);
    }

    @Override
    public List<MedicalReportResponseDTO> getReportsByPatient(Long patientId) {
        return repository.findByPatientId(patientId).stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<MedicalReportResponseDTO> getReportsByProvider(Long providerId) {
        return repository.findByProviderId(providerId).stream().map(this::mapToDTO).toList();
    }

    private MedicalReportResponseDTO mapToDTO(MedicalReport report) {
        return MedicalReportResponseDTO.builder()
                .reportId(report.getReportId())
                .appointmentId(report.getAppointmentId())
                .patientId(report.getPatientId())
                .providerId(report.getProviderId())
                .providerName(report.getProviderName())
                .diagnosis(report.getDiagnosis())
                .prescription(report.getPrescription())
                .notes(report.getNotes())
                .reportDate(report.getReportDate())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
