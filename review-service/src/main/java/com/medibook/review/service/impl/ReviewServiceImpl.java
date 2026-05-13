package com.medibook.review.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medibook.review.dto.ReviewRequestDTO;
import com.medibook.review.dto.ReviewResponseDTO;
import com.medibook.review.entity.Review;
import com.medibook.review.exception.BadRequestException;
import com.medibook.review.exception.ResourceNotFoundException;
import com.medibook.review.repository.ReviewRepository;
import com.medibook.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;

    @Override
    public ReviewResponseDTO addReview(ReviewRequestDTO request) {
        log.info("Adding review for appointmentId: {}", request.getAppointmentId());

        if (repository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new BadRequestException("Review already exists for this appointment");
        }

        Review review = Review.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .rating(request.getRating())
                .comment(request.getComment())
                .isAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false)
                .isVerified(false)
                .build();

        return mapToDTO(repository.save(review));
    }

    @Override
    public List<ReviewResponseDTO> getByProvider(Long providerId) {
        return repository.findByProviderId(providerId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> getByPatient(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Optional<ReviewResponseDTO> getByAppointment(Long appointmentId) {
        return repository.findByAppointmentId(appointmentId)
                .map(this::mapToDTO);
    }

    @Override
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO request) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        if (request.getIsAnonymous() != null) {
            review.setIsAnonymous(request.getIsAnonymous());
        }

        return mapToDTO(repository.save(review));
    }

    @Override
    public void deleteReview(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found");
        }

        repository.deleteById(id);
    }

    @Override
    public Double getAvgRating(Long providerId) {
        List<Review> reviews = repository.findByProviderId(providerId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        return reviews.stream()
                .mapToLong(Review::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public Long getReviewCount(Long providerId) {
        return repository.countByProviderId(providerId);
    }

    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private ReviewResponseDTO mapToDTO(Review review) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .appointmentId(review.getAppointmentId())
                .patientId(review.getPatientId())
                .providerId(review.getProviderId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .isVerified(review.getIsVerified())
                .isAnonymous(review.getIsAnonymous())
                .build();
    }
}