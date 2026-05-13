package com.medibook.schedule.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.medibook.schedule.entity.AvailabilitySlot;

public interface ScheduleService {

    AvailabilitySlot addSlot(AvailabilitySlot slot);

    List<AvailabilitySlot> addBulkSlots(List<AvailabilitySlot> slots);

    List<AvailabilitySlot> getSlotsByProvider(Long id);

    List<AvailabilitySlot> getAvailabilitySlots(Long id, LocalDate date);

    Optional<AvailabilitySlot> getSlotById(Long id);

    void bookSlot(Long id);

    void unblockSlot(Long id);

    void deleteSlot(Long id);

    AvailabilitySlot updateSlot(Long id, AvailabilitySlot slot);

    void blockSlot(Long id);

    List<AvailabilitySlot> generateRecurringSlots(Long id, String recurrence, LocalDate date);
}