package com.medibook.review.service;

import java.util.List;
import java.util.Optional;

import com.medibook.review.dto.ReviewRequestDTO;
import com.medibook.review.dto.ReviewResponseDTO;

public interface ReviewService {

    ReviewResponseDTO addReview(ReviewRequestDTO request);

    List<ReviewResponseDTO> getByProvider(Long providerId);

    List<ReviewResponseDTO> getByPatient(Long patientId);

    Optional<ReviewResponseDTO> getByAppointment(Long appointmentId);

    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO request);

    void deleteReview(Long id);

    Double getAvgRating(Long providerId);

    Long getReviewCount(Long providerId);

    List<ReviewResponseDTO> getAllReviews();
}