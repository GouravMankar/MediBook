package com.medibook.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.review.dto.ReviewRequestDTO;
import com.medibook.review.dto.ReviewResponseDTO;
import com.medibook.review.service.ReviewService;

@WebMvcTest(controllers = ReviewController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService service;

    @Test
    void addReviewReturnsCreatedReview() throws Exception {
        when(service.addReview(any(ReviewRequestDTO.class))).thenReturn(review());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentId": 1,
                                  "patientId": 2,
                                  "providerId": 3,
                                  "rating": 5,
                                  "comment": "Excellent"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getProviderAverageRatingReturnsValue() throws Exception {
        when(service.getAvgRating(3L)).thenReturn(4.5);

        mockMvc.perform(get("/reviews/provider/3/avg-rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4.5));
    }

    @Test
    void getReviewsByProviderReturnsList() throws Exception {
        when(service.getByProvider(3L)).thenReturn(List.of(review()));

        mockMvc.perform(get("/reviews/provider/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value(3));
    }

    @Test
    void getReviewsByPatientReturnsList() throws Exception {
        when(service.getByPatient(2L)).thenReturn(List.of(review()));

        mockMvc.perform(get("/reviews/patient/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(2));
    }

    @Test
    void getReviewByAppointmentReturnsReviewWhenPresent() throws Exception {
        when(service.getByAppointment(1L)).thenReturn(Optional.of(review()));

        mockMvc.perform(get("/reviews/appointment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1));
    }

    @Test
    void getReviewByAppointmentReturnsNotFoundWhenMissing() throws Exception {
        when(service.getByAppointment(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reviews/appointment/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReviewReturnsUpdatedReview() throws Exception {
        when(service.updateReview(any(Long.class), any(ReviewRequestDTO.class))).thenReturn(review());

        mockMvc.perform(put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validReviewJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1));
    }

    @Test
    void deleteReviewReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted successfully"));

        verify(service).deleteReview(1L);
    }

    @Test
    void getReviewCountReturnsValue() throws Exception {
        when(service.getReviewCount(3L)).thenReturn(12L);

        mockMvc.perform(get("/reviews/provider/3/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(12));
    }

    @Test
    void getAllReviewsReturnsList() throws Exception {
        when(service.getAllReviews()).thenReturn(List.of(review()));

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(1));
    }

    @Test
    void invalidReviewPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private String validReviewJson() {
        return """
                {
                  "appointmentId": 1,
                  "patientId": 2,
                  "providerId": 3,
                  "rating": 5,
                  "comment": "Excellent"
                }
                """;
    }

    private ReviewResponseDTO review() {
        return ReviewResponseDTO.builder()
                .reviewId(1L)
                .appointmentId(1L)
                .patientId(2L)
                .providerId(3L)
                .rating(5L)
                .comment("Excellent")
                .build();
    }
}
