package com.medibook.appointment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "schedule-service")
public interface ScheduleClient {

    @PutMapping("/slots/{id}/book")
    String bookSlot(@PathVariable("id") Long id);

    @PutMapping("/slots/{id}/unblock")
    String unblockSlot(@PathVariable("id") Long id);

    @PutMapping("/slots/{id}/unblock")
    String unbookSlot(@PathVariable("id") Long id);

    @PutMapping("/slots/{id}/block")
    String blockSlot(@PathVariable("id") Long id);
}
