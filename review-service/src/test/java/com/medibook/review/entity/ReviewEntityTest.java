package com.medibook.review.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ReviewEntityTest {

    @Test
    void prePersistSetsDefaultsWhenValuesAreMissing() {
        Review review = new Review();

        review.prePersist();

        assertThat(review.getReviewDate()).isEqualTo(LocalDate.now());
        assertThat(review.getIsVerified()).isFalse();
        assertThat(review.getIsAnonymous()).isFalse();
    }

    @Test
    void prePersistKeepsExistingValues() {
        LocalDate date = LocalDate.of(2026, 5, 11);
        Review review = Review.builder()
                .reviewDate(date)
                .isVerified(true)
                .isAnonymous(true)
                .build();

        review.prePersist();

        assertThat(review.getReviewDate()).isEqualTo(date);
        assertThat(review.getIsVerified()).isTrue();
        assertThat(review.getIsAnonymous()).isTrue();
    }
}
