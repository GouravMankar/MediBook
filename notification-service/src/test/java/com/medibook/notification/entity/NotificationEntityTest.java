package com.medibook.notification.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NotificationEntityTest {

    @Test
    void prePersistSetsDefaultsWhenValuesAreMissing() {
        Notification notification = new Notification();

        notification.prePersist();

        assertThat(notification.getSentAt()).isNotNull();
        assertThat(notification.getIsRead()).isFalse();
    }

    @Test
    void prePersistKeepsExistingValues() {
        LocalDateTime sentAt = LocalDateTime.of(2026, 5, 11, 10, 0);
        Notification notification = Notification.builder()
                .sentAt(sentAt)
                .isRead(true)
                .build();

        notification.prePersist();

        assertThat(notification.getSentAt()).isEqualTo(sentAt);
        assertThat(notification.getIsRead()).isTrue();
    }
}
