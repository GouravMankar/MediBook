package com.medibook.review.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.review.dto.ReviewRequestDTO;
import com.medibook.review.dto.ReviewResponseDTO;
import com.medibook.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> addReview(@Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.ok(service.addReview(request));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(service.getByProvider(providerId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getByPatient(patientId));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ReviewResponseDTO> getByAppointment(@PathVariable Long appointmentId) {
        Optional<ReviewResponseDTO> review = service.getByAppointment(appointmentId);
        return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.ok(service.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        service.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }

    @GetMapping("/provider/{providerId}/avg-rating")
    public ResponseEntity<Double> getAvgRating(@PathVariable Long providerId) {
        return ResponseEntity.ok(service.getAvgRating(providerId));
    }

    @GetMapping("/provider/{providerId}/count")
    public ResponseEntity<Long> getReviewCount(@PathVariable Long providerId) {
        return ResponseEntity.ok(service.getReviewCount(providerId));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(service.getAllReviews());
    }
}