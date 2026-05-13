package com.medibook.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleResponseDTO {

    private Long slotId;
    private Long providerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Boolean isBooked;
    private Boolean isBlocked;
    private String recurrence;
}