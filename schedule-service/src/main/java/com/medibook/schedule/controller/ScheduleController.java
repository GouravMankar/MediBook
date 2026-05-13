package com.medibook.schedule.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 1. Add single slot
    @PostMapping("/single")
    public ResponseEntity<AvailabilitySlot> addSlot(@RequestBody AvailabilitySlot slot) {
        log.info("Request received to add slot for providerId: {}", slot.getProviderId());
        return ResponseEntity.ok(scheduleService.addSlot(slot));
    }

    // 2. Add bulk slots
    @PostMapping("/bulk")
    public ResponseEntity<List<AvailabilitySlot>> addBulkSlots(@RequestBody List<AvailabilitySlot> slots) {
        log.info("Request received to add bulk slots, count: {}", slots.size());
        return ResponseEntity.ok(scheduleService.addBulkSlots(slots));
    }

    // 3. Get all slots by provider
    @GetMapping("/provider/{id}")
    public ResponseEntity<List<AvailabilitySlot>> getSlotsByProvider(@PathVariable Long id) {
        log.info("Request received to fetch slots for providerId: {}", id);
        return ResponseEntity.ok(scheduleService.getSlotsByProvider(id));
    }

    // 4. Get available slots by provider and date
    @GetMapping("/available/{id}")
    public ResponseEntity<List<AvailabilitySlot>> getAvailabilitySlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Request received to fetch available slots for providerId: {} on date: {}", id, date);
        return ResponseEntity.ok(scheduleService.getAvailabilitySlots(id, date));
    }

    // 5. Get slot by slotId
    @GetMapping("/{id}")
    public ResponseEntity<AvailabilitySlot> getSlotById(@PathVariable Long id) {
        log.info("Request received to fetch slot by id: {}", id);

        Optional<AvailabilitySlot> slot = scheduleService.getSlotById(id);
        return slot.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. Book slot
    @PutMapping("/{id}/book")
    public ResponseEntity<String> bookSlot(@PathVariable Long id) {
        log.info("Request received to book slot id: {}", id);
        scheduleService.bookSlot(id);
        return ResponseEntity.ok("Slot booked successfully");
    }

    // 7. Unblock slot
    @PutMapping("/{id}/unblock")
    public ResponseEntity<String> unblockSlot(@PathVariable Long id) {
        log.info("Request received to unblock slot id: {}", id);
        scheduleService.unblockSlot(id);
        return ResponseEntity.ok("Slot unblocked successfully");
    }

    // 8. Delete slot
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSlot(@PathVariable Long id) {
        log.info("Request received to delete slot id: {}", id);
        scheduleService.deleteSlot(id);
        return ResponseEntity.ok("Slot deleted successfully");
    }

    // 9. Update slot
    @PutMapping("/{id}")
    public ResponseEntity<AvailabilitySlot> updateSlot(
            @PathVariable Long id,
            @RequestBody AvailabilitySlot slot) {

        log.info("Request received to update slot id: {}", id);
        return ResponseEntity.ok(scheduleService.updateSlot(id, slot));
    }

    // 10. Block slot
    @PutMapping("/{id}/block")
    public ResponseEntity<String> blockSlot(@PathVariable Long id) {
        log.info("Request received to block slot id: {}", id);
        scheduleService.blockSlot(id);
        return ResponseEntity.ok("Slot blocked successfully");
    }

    // 11. Generate recurring slots
    @PostMapping("/recurring/{providerId}")
    public ResponseEntity<List<AvailabilitySlot>> generateRecurringSlots(
            @PathVariable Long providerId,
            @RequestParam String recurrence,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Request received to generate recurring slots for providerId: {}, recurrence: {}, date: {}",
                providerId, recurrence, date);

        return ResponseEntity.ok(
                scheduleService.generateRecurringSlots(providerId, recurrence, date)
        );
    }
}