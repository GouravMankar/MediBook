package com.medibook.appointment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class AppointmentEntityTest {

    @Test
    void prePersistSetsDefaultsWhenValuesAreMissing() {
        Appointment appointment = new Appointment();

        appointment.prePersist();

        assertThat(appointment.getCreatedAt()).isNotNull();
        assertThat(appointment.getStatus()).isEqualTo("SCHEDULED");
    }

    @Test
    void prePersistKeepsExistingValues() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 11, 10, 0);
        Appointment appointment = Appointment.builder()
                .createdAt(createdAt)
                .status("CONFIRMED")
                .build();

        appointment.prePersist();

        assertThat(appointment.getCreatedAt()).isEqualTo(createdAt);
        assertThat(appointment.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void preUpdateSetsUpdatedAt() {
        Appointment appointment = new Appointment();

        appointment.preUpdate();

        assertThat(appointment.getUpdatedAt()).isNotNull();
    }
}
