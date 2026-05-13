package com.medibook.provider.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProviderRequestDTO {

    @NotNull(message = "UserId is required")
    private Long userId;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String qualification;

    private Integer experienceYears;

    private String bio;

    private String clinicName;

    private String clinicAddress;

    private Double consultationFee;

    private Double fee;
}
