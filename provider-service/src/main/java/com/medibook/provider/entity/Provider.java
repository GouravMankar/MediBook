package com.medibook.provider.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "providers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    // Reference to auth-service User
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String specialization;

    private String qualification;

    private Integer experienceYears;

    @Column(length = 500)
    private String bio;

    private String clinicName;

    private String clinicAddress;

    private Double consultationFee;

    private Double avgRating;

    private Boolean isAvailable;

    private Boolean isVerified;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.avgRating == null) {
            this.avgRating = 0.0;
        }

        if (this.isAvailable == null) {
            this.isAvailable = true;
        }

        if (this.isVerified == null) {
            this.isVerified = false;
        }
    }

	public boolean isAvailable() {
		return this.isAvailable;
	}
	public boolean isVerified() {
		return this.isVerified;
	}
	
	
}
