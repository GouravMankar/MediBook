package com.medibook.review.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false, unique = true)
    private Long appointmentId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long providerId;

    @Column(nullable = false)
    private Long rating;

    private String comment;

    private LocalDate reviewDate;

    private Boolean isVerified;

    private Boolean isAnonymous;

    @PrePersist
    public void prePersist() {
        if (reviewDate == null) reviewDate = LocalDate.now();
        if (isVerified == null) isVerified = false;
        if (isAnonymous == null) isAnonymous = false;
    }
}