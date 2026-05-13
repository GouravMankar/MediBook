package com.medibook.schedule.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class AvailabilitySlotTest {

    @Test
    void prePersistSetsDefaultsWhenValuesAreMissing() {
        AvailabilitySlot slot = new AvailabilitySlot();

        slot.prePersist();

        assertThat(slot.getCreatedAt()).isNotNull();
        assertThat(slot.getIsBooked()).isFalse();
        assertThat(slot.getIsBlocked()).isFalse();
    }

    @Test
    void prePersistKeepsExistingValues() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 11, 10, 0);
        AvailabilitySlot slot = AvailabilitySlot.builder()
                .createdAt(createdAt)
                .isBooked(true)
                .isBlocked(true)
                .build();

        slot.prePersist();

        assertThat(slot.getCreatedAt()).isEqualTo(createdAt);
        assertThat(slot.getIsBooked()).isTrue();
        assertThat(slot.getIsBlocked()).isTrue();
    }
}
