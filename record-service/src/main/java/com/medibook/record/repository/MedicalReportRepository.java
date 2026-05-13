package com.medibook.record.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medibook.record.entity.MedicalReport;

public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {

    List<MedicalReport> findByPatientId(Long patientId);

    List<MedicalReport> findByProviderId(Long providerId);

    Optional<MedicalReport> findByAppointmentId(Long appointmentId);
}
