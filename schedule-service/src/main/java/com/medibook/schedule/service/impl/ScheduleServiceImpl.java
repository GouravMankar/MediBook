package com.medibook.schedule.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.repository.SlotRepository;
import com.medibook.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final SlotRepository repository;

    @Override
    public AvailabilitySlot addSlot(AvailabilitySlot slot) {
        log.info("Adding single slot for providerId: {}", slot.getProviderId());

        if (slot.getProviderId() == null) {
            throw new RuntimeException("ProviderId is required");
        }
        if (slot.getDate() == null) {
            throw new RuntimeException("Date is required");
        }
        if (slot.getStartTime() == null || slot.getEndTime() == null) {
            throw new RuntimeException("Start time and end time are required");
        }
        if (slot.getDurationMinutes() == null) {
            throw new RuntimeException("Duration is required");
        }

        return repository.save(slot);
    }

    @Override
    public List<AvailabilitySlot> addBulkSlots(List<AvailabilitySlot> slots) {
        log.info("Adding bulk slots, count: {}", slots.size());

        if (slots == null || slots.isEmpty()) {
            throw new RuntimeException("Slot list cannot be empty");
        }

        return repository.saveAll(slots);
    }

    @Override
    public List<AvailabilitySlot> getSlotsByProvider(Long id) {
        log.info("Fetching all slots for providerId: {}", id);

        return repository.findByProviderId(id);
    }

    @Override
    public List<AvailabilitySlot> getAvailabilitySlots(Long id, LocalDate date) {
        log.info("Fetching available slots for providerId: {} on date: {}", id, date);

        return repository.findByProviderIdAndDateAndIsBlockedFalseAndIsBookedFalse(id, date);
    }

    @Override
    public Optional<AvailabilitySlot> getSlotById(Long id) {
        log.info("Fetching slot by id: {}", id);

        return repository.findBySlotId(id);
    }

    @Override
    public void bookSlot(Long id) {
        AvailabilitySlot slot = repository.findBySlotId(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (Boolean.TRUE.equals(slot.getIsBlocked())) {
            throw new RuntimeException("Blocked slot cannot be booked");
        }

        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new RuntimeException("Slot is already booked");
        }

        slot.setIsBooked(true);
        repository.save(slot);

        log.info("Slot booked successfully: {}", id);
    }
    @Override
    public void unblockSlot(Long id) {
        AvailabilitySlot slot = repository.findBySlotId(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        slot.setIsBlocked(false);
        slot.setIsBooked(false);

        repository.save(slot);

        log.info("Slot released successfully: {}", id);
    }

    @Override
    public void deleteSlot(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Slot not found");
        }

        repository.deleteById(id);

        log.info("Slot deleted successfully: {}", id);
    }

    @Override
    public AvailabilitySlot updateSlot(Long id, AvailabilitySlot slot) {
        AvailabilitySlot existingSlot = repository.findBySlotId(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        existingSlot.setDate(slot.getDate());
        existingSlot.setStartTime(slot.getStartTime());
        existingSlot.setEndTime(slot.getEndTime());
        existingSlot.setDurationMinutes(slot.getDurationMinutes());
        existingSlot.setRecurrence(slot.getRecurrence());

        AvailabilitySlot updated = repository.save(existingSlot);

        log.info("Slot updated successfully: {}", id);

        return updated;
    }

    @Override
    public void blockSlot(Long id) {
        AvailabilitySlot slot = repository.findBySlotId(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        slot.setIsBlocked(true);
        repository.save(slot);

        log.info("Slot blocked successfully: {}", id);
    }

    @Override
    public List<AvailabilitySlot> generateRecurringSlots(Long providerId, String recurrence, LocalDate startDate) {
        log.info("Generating recurring slots for providerId: {}, recurrence: {}, startDate: {}",
                providerId, recurrence, startDate);

        List<AvailabilitySlot> generatedSlots = new ArrayList<>();

        if (providerId == null) {
            throw new RuntimeException("ProviderId is required");
        }

        if (recurrence == null || recurrence.isBlank()) {
            throw new RuntimeException("Recurrence is required");
        }

        if (startDate == null) {
            throw new RuntimeException("Start date is required");
        }

      
        if ("DAILY".equalsIgnoreCase(recurrence)) {
            for (int i = 0; i < 7; i++) {
                AvailabilitySlot slot = AvailabilitySlot.builder()
                        .providerId(providerId)
                        .date(startDate.plusDays(i))
                        .startTime(java.time.LocalTime.of(10, 0))
                        .endTime(java.time.LocalTime.of(10, 30))
                        .durationMinutes(30)
                        .recurrence("DAILY")
                        .isBooked(false)
                        .isBlocked(false)
                        .build();

                generatedSlots.add(slot);
            }
        } else if ("WEEKLY".equalsIgnoreCase(recurrence)) {
            for (int i = 0; i < 4; i++) {
                AvailabilitySlot slot = AvailabilitySlot.builder()
                        .providerId(providerId)
                        .date(startDate.plusWeeks(i))
                        .startTime(java.time.LocalTime.of(10, 0))
                        .endTime(java.time.LocalTime.of(10, 30))
                        .durationMinutes(30)
                        .recurrence("WEEKLY")
                        .isBooked(false)
                        .isBlocked(false)
                        .build();

                generatedSlots.add(slot);
            }
        } else {
            throw new RuntimeException("Unsupported recurrence type. Use DAILY or WEEKLY");
        }

        return repository.saveAll(generatedSlots);
    }
}