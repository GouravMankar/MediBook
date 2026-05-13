package com.medibook.payment.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEventDTO {

    private Long recipientId;
    private String type;
    private String title;
    private String message;
    private String channel;
    private Long relatedId;
    private String relatedType;
}