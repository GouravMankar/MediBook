package com.medibook.record.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MedicalRecordEntityTest {

    @Test
    void prePersistSetsCreatedAtWhenMissing() {
        MedicalRecord record = new MedicalRecord();

        record.prePersist();

        assertThat(record.getCreatedAt()).isNotNull();
    }

    @Test
    void prePersistKeepsExistingCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 11, 10, 0);
        MedicalRecord record = MedicalRecord.builder().createdAt(createdAt).build();

        record.prePersist();

        assertThat(record.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void preUpdateSetsUpdatedAt() {
        MedicalRecord record = new MedicalRecord();

        record.preUpdate();

        assertThat(record.getUpdatedAt()).isNotNull();
    }
}
