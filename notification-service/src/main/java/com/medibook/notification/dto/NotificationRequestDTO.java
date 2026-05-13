package com.medibook.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDTO {

    @NotNull(message = "RecipientId is required")
    private Long recipientId;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Channel is required")
    private String channel;

    private Long relatedId;
    private String relatedType;
}