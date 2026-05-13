package com.medibook.notification.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.medibook.notification.dto.NotificationEventDTO;
import com.medibook.notification.dto.NotificationRequestDTO;
import com.medibook.notification.service.NotificationService;

class NotificationConsumerTest {

    private final NotificationService service = Mockito.mock(NotificationService.class);
    private final NotificationConsumer consumer = new NotificationConsumer(service);

    @Test
    void consumeMapsEventToNotificationRequest() {
        NotificationEventDTO event = NotificationEventDTO.builder()
                .recipientId(1L)
                .type("REMINDER")
                .title("Appointment")
                .message("Visit soon")
                .channel("EMAIL")
                .relatedId(9L)
                .relatedType("APPOINTMENT")
                .build();

        consumer.consume(event);

        ArgumentCaptor<NotificationRequestDTO> captor = ArgumentCaptor.forClass(NotificationRequestDTO.class);
        verify(service).send(captor.capture());
        assertThat(captor.getValue().getRecipientId()).isEqualTo(1L);
        assertThat(captor.getValue().getChannel()).isEqualTo("EMAIL");
        assertThat(captor.getValue().getRelatedId()).isEqualTo(9L);
    }

    @Test
    void consumeDefaultsMissingChannelToApp() {
        NotificationEventDTO event = NotificationEventDTO.builder()
                .recipientId(1L)
                .type("REMINDER")
                .title("Appointment")
                .message("Visit soon")
                .build();

        consumer.consume(event);

        ArgumentCaptor<NotificationRequestDTO> captor = ArgumentCaptor.forClass(NotificationRequestDTO.class);
        verify(service).send(captor.capture());
        assertThat(captor.getValue().getChannel()).isEqualTo("APP");
    }
}
