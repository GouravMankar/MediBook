package com.medibook.notification.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BulkNotificationRequestDTO {

    @NotEmpty(message = "Recipients list cannot be empty")
    private List<Long> recipients;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private String type;
    private String channel;
    private Long relatedId;
    private String relatedType;
}