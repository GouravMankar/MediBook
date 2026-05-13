package com.medibook.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {

    @NotNull
    private Long appointmentId;

    @NotNull
    private Long patientId;

    @NotNull
    private Long providerId;

    @NotNull
    @Min(1)
    @Max(5)
    private Long rating;

    private String comment;
    private Boolean isAnonymous;
}