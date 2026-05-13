package com.medibook.appointment.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.appointment.dto.AppointmentRequestDTO;
import com.medibook.appointment.dto.AppointmentResponseDTO;
import com.medibook.appointment.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            @Valid @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity.ok(service.bookAppointment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getById(@PathVariable Long id) {
        Optional<AppointmentResponseDTO> appointment = service.getById(id);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(service.getByProvider(providerId));
    }

    @GetMapping("/provider/{providerId}/date")
    public ResponseEntity<List<AppointmentResponseDTO>> getByProviderAndDate(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getByProviderAndDate(providerId, date));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelAppointment(id));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponseDTO> rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity.ok(service.rescheduleAppointment(id, request));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(service.completeAppointment(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<List<AppointmentResponseDTO>> getUpcomingByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getUpcomingByPatient(patientId));
    }

    @GetMapping("/count/{providerId}")
    public ResponseEntity<Long> getAppointmentCount(@PathVariable Long providerId) {
        return ResponseEntity.ok(service.getAppointmentCount(providerId));
    }
}
