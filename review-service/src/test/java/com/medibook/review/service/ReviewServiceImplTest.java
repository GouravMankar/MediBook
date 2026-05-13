package com.medibook.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medibook.review.dto.ReviewRequestDTO;
import com.medibook.review.dto.ReviewResponseDTO;
import com.medibook.review.entity.Review;
import com.medibook.review.exception.BadRequestException;
import com.medibook.review.exception.ResourceNotFoundException;
import com.medibook.review.repository.ReviewRepository;
import com.medibook.review.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository repository;

    @InjectMocks
    private ReviewServiceImpl service;

    @Test
    void addReviewCreatesReviewWhenAppointmentHasNoReview() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setAppointmentId(4L);
        request.setPatientId(5L);
        request.setProviderId(6L);
        request.setRating(5L);
        request.setComment("Great consultation");

        when(repository.findByAppointmentId(4L)).thenReturn(Optional.empty());
        when(repository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setReviewId(20L);
            return review;
        });

        ReviewResponseDTO response = service.addReview(request);

        assertThat(response.getReviewId()).isEqualTo(20L);
        assertThat(response.getRating()).isEqualTo(5L);
        assertThat(response.getIsVerified()).isFalse();
    }

    @Test
    void addReviewRejectsDuplicateAppointmentReview() {
        when(repository.findByAppointmentId(4L)).thenReturn(Optional.of(Review.builder().reviewId(1L).build()));
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setAppointmentId(4L);

        assertThatThrownBy(() -> service.addReview(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getAvgRatingReturnsAverageOrZero() {
        when(repository.findByProviderId(8L)).thenReturn(List.of(
                Review.builder().rating(5L).build(),
                Review.builder().rating(3L).build()));
        when(repository.findByProviderId(99L)).thenReturn(List.of());

        assertThat(service.getAvgRating(8L)).isEqualTo(4.0);
        assertThat(service.getAvgRating(99L)).isEqualTo(0.0);
    }

    @Test
    void updateReviewChangesRatingCommentAndAnonymousFlag() {
        Review review = Review.builder().reviewId(9L).rating(3L).comment("Ok").isAnonymous(false).build();
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRating(5L);
        request.setComment("Great");
        request.setIsAnonymous(true);
        when(repository.findById(9L)).thenReturn(Optional.of(review));
        when(repository.save(review)).thenReturn(review);

        ReviewResponseDTO response = service.updateReview(9L, request);

        assertThat(response.getRating()).isEqualTo(5L);
        assertThat(response.getComment()).isEqualTo("Great");
        assertThat(response.getIsAnonymous()).isTrue();
    }

    @Test
    void retrievalDeleteAndCountMethodsUseRepository() {
        Review review = Review.builder()
                .reviewId(1L)
                .appointmentId(4L)
                .patientId(5L)
                .providerId(6L)
                .rating(5L)
                .comment("Great")
                .build();
        when(repository.findByProviderId(6L)).thenReturn(List.of(review));
        when(repository.findByPatientId(5L)).thenReturn(List.of(review));
        when(repository.findByAppointmentId(4L)).thenReturn(Optional.of(review));
        when(repository.findAll()).thenReturn(List.of(review));
        when(repository.countByProviderId(6L)).thenReturn(3L);
        when(repository.existsById(1L)).thenReturn(true);

        assertThat(service.getByProvider(6L)).hasSize(1);
        assertThat(service.getByPatient(5L)).hasSize(1);
        assertThat(service.getByAppointment(4L)).isPresent();
        assertThat(service.getAllReviews()).hasSize(1);
        assertThat(service.getReviewCount(6L)).isEqualTo(3L);
        service.deleteReview(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteReviewRejectsMissingReview() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteReview(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
