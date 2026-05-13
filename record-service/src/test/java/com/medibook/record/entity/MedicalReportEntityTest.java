package com.medibook.record.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MedicalReportEntityTest {

    @Test
    void prePersistSetsCreatedAtAndReportDateWhenMissing() {
        MedicalReport report = new MedicalReport();

        report.prePersist();

        assertThat(report.getCreatedAt()).isNotNull();
        assertThat(report.getReportDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void prePersistKeepsExistingDates() {
        LocalDate reportDate = LocalDate.of(2026, 5, 11);
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 11, 10, 30);
        MedicalReport report = MedicalReport.builder()
                .reportDate(reportDate)
                .createdAt(createdAt)
                .build();

        report.prePersist();

        assertThat(report.getCreatedAt()).isEqualTo(createdAt);
        assertThat(report.getReportDate()).isEqualTo(reportDate);
    }
}
