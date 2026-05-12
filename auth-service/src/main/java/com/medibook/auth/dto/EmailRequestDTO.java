package com.medibook.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailRequestDTO {
    private String email;
    private String subject;
    private String message;
}
