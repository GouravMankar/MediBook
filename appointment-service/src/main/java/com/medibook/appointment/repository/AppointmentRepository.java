package com.medibook.appointment.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medibook.appointment.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	List<Appointment> findByPatientId(Long patientId);

	List<Appointment> findByProviderId(Long providerId);

	List<Appointment> findByProviderIdAndAppointmentDate(Long providerId, LocalDate appointmentDate);

	List<Appointment> findByPatientIdAndAppointmentDateAfter(Long patientId, LocalDate date);

	Optional<Appointment> findBySlotId(Long slotId);

	long countByProviderId(Long providerId);

	boolean existsBySlotIdAndStatusIn(Long slotId, List<String> statuses);

	List<Appointment> findByPatientIdAndAppointmentDateGreaterThanEqualAndStatusIgnoreCase(Long patientId,
			LocalDate date, String status);

	@Query("""
			select a from Appointment a
			where a.patientId = :patientId
			  and upper(a.status) = upper(:status)
			  and a.appointmentDate >= :date
			""")
	List<Appointment> findUpcomingByPatient(@Param("patientId") Long patientId,
			@Param("date") LocalDate date,
			@Param("status") String status);
}
