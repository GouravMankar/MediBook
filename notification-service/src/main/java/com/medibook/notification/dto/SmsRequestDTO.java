package com.medibook.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsRequestDTO {

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Message is required")
    private String message;
}