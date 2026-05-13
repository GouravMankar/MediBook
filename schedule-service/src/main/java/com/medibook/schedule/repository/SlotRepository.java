package com.medibook.schedule.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medibook.schedule.entity.AvailabilitySlot;

public interface SlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByProviderId(Long providerId);

    List<AvailabilitySlot> findByProviderIdAndDate(Long providerId, LocalDate date);

    List<AvailabilitySlot> findByProviderIdAndDateAndIsBlockedFalseAndIsBookedFalse(Long providerId, LocalDate date);

    Optional<AvailabilitySlot> findBySlotId(Long slotId);
}