package com.medibook.record.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.medibook.record.dto.AppointmentResponseDTO;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @GetMapping("/appointments/{id}")
    AppointmentResponseDTO getAppointment(@PathVariable Long id);
}
