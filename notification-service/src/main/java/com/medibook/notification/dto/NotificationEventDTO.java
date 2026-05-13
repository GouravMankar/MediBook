package com.medibook.notification.dto;

import java.io.Serializable;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEventDTO implements Serializable {

    private Long recipientId;
    private String type;
    private String title;
    private String message;
    private String channel;
    private Long relatedId;
    private String relatedType;
}