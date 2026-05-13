package com.medibook.notification.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponseDTO {

    private Long notificationId;
    private Long recipientId;
    private String type;
    private String title;
    private String message;
    private String channel;
    private Long relatedId;
    private String relatedType;
    private Boolean isRead;
    private LocalDateTime sentAt;
}