package com.medibook.record.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.record.dto.MedicalRecordRequestDTO;
import com.medibook.record.dto.MedicalRecordResponseDTO;
import com.medibook.record.service.MedicalRecordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService service;

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createRecord(
            @Valid @RequestBody MedicalRecordRequestDTO request) {
        return ResponseEntity.ok(service.createRecord(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getRecordById(@PathVariable Long id) {
        Optional<MedicalRecordResponseDTO> record = service.getRecordById(id);
        return record.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordResponseDTO> getRecordByAppointment(
            @PathVariable Long appointmentId) {
        Optional<MedicalRecordResponseDTO> record = service.getRecordByAppointment(appointmentId);
        return record.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(service.getRecordsByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsByProvider(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(service.getRecordsByProvider(providerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordRequestDTO request) {
        return ResponseEntity.ok(service.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id) {
        service.deleteRecord(id);
        return ResponseEntity.ok("Medical record deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllRecords() {
        return ResponseEntity.ok(service.getAllRecords());
    }
}