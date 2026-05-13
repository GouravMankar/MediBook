package com.medibook.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.medibook.notification.config.RabbitMQConfig;
import com.medibook.notification.dto.NotificationEventDTO;
import com.medibook.notification.dto.NotificationRequestDTO;
import com.medibook.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consume(NotificationEventDTO event) {
        log.info("Notification event received: {}", event);

        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setRecipientId(event.getRecipientId());
        request.setType(event.getType());
        request.setTitle(event.getTitle());
        request.setMessage(event.getMessage());
        request.setChannel(event.getChannel() != null ? event.getChannel() : "APP");
        request.setRelatedId(event.getRelatedId());
        request.setRelatedType(event.getRelatedType());

        notificationService.send(request);
    }
}