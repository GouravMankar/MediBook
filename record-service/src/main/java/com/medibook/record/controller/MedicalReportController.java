package com.medibook.record.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.record.dto.MedicalReportRequestDTO;
import com.medibook.record.dto.MedicalReportResponseDTO;
import com.medibook.record.service.MedicalReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class MedicalReportController {

    private final MedicalReportService service;

    @PostMapping
    public ResponseEntity<MedicalReportResponseDTO> createReport(
            @Valid @RequestBody MedicalReportRequestDTO request) {
        return ResponseEntity.ok(service.createReport(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalReportResponseDTO> getReportById(@PathVariable Long id) {
        return service.getReportById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalReportResponseDTO>> getReportsByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(service.getReportsByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<MedicalReportResponseDTO>> getReportsByProvider(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(service.getReportsByProvider(providerId));
    }
}
