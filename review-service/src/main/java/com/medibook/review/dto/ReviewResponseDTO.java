package com.medibook.review.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponseDTO {

    private Long reviewId;
    private Long appointmentId;
    private Long patientId;
    private Long providerId;
    private Long rating;
    private String comment;
    private LocalDate reviewDate;
    private Boolean isVerified;
    private Boolean isAnonymous;
}