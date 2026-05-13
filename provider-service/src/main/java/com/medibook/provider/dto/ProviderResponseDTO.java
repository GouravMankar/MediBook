package com.medibook.provider.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderResponseDTO {

	private Long providerId;

	private Long userId;

	private String providerName;
	
	private String specialization;

	private String qualification;

	private Integer experienceYears;

	private String bio;

	private String clinicName;

	private String clinicAddress;

	private Double consultationFee;

	private Double fee;

	private Double avgRating;

	private Boolean isAvailable;

	private Boolean isVerified;
}
