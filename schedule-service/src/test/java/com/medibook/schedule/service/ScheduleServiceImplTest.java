package com.medibook.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.repository.SlotRepository;
import com.medibook.schedule.service.impl.ScheduleServiceImpl;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private SlotRepository repository;

    @InjectMocks
    private ScheduleServiceImpl service;

    @Test
    void bookSlotMarksAvailableSlotAsBooked() {
        AvailabilitySlot slot = slot(9L, false, false);
        when(repository.findBySlotId(9L)).thenReturn(Optional.of(slot));
        when(repository.save(slot)).thenReturn(slot);

        service.bookSlot(9L);

        assertThat(slot.getIsBooked()).isTrue();
        verify(repository).save(slot);
    }

    @Test
    void bookSlotRejectsBlockedSlots() {
        when(repository.findBySlotId(10L)).thenReturn(Optional.of(slot(10L, false, true)));

        assertThatThrownBy(() -> service.bookSlot(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Blocked slot");
    }

    @Test
    void bookSlotRejectsAlreadyBookedSlotsAndMissingSlots() {
        when(repository.findBySlotId(11L)).thenReturn(Optional.of(slot(11L, true, false)));
        when(repository.findBySlotId(12L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.bookSlot(11L))
                .hasMessageContaining("already booked");
        assertThatThrownBy(() -> service.bookSlot(12L))
                .hasMessageContaining("not found");
    }

    @Test
    void generateDailyRecurringSlotsCreatesSevenSlots() {
        LocalDate startDate = LocalDate.of(2026, 5, 11);
        when(repository.saveAll(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AvailabilitySlot> slots = service.generateRecurringSlots(3L, "DAILY", startDate);

        assertThat(slots).hasSize(7);
        assertThat(slots.get(0).getDate()).isEqualTo(startDate);
        assertThat(slots.get(6).getDate()).isEqualTo(startDate.plusDays(6));
    }

    @Test
    void addSlotValidatesRequiredFieldsAndSavesValidSlot() {
        assertThatThrownBy(() -> service.addSlot(AvailabilitySlot.builder().build()))
                .hasMessageContaining("ProviderId");

        AvailabilitySlot slot = slot(1L, false, false);
        when(repository.save(slot)).thenReturn(slot);

        assertThat(service.addSlot(slot)).isSameAs(slot);
    }

    @Test
    void addBulkDeleteAndRecurringValidateInput() {
        assertThatThrownBy(() -> service.addBulkSlots(List.of()))
                .hasMessageContaining("cannot be empty");
        when(repository.existsById(44L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteSlot(44L))
                .hasMessageContaining("not found");
        assertThatThrownBy(() -> service.generateRecurringSlots(null, "DAILY", LocalDate.now()))
                .hasMessageContaining("ProviderId");
        assertThatThrownBy(() -> service.generateRecurringSlots(1L, " ", LocalDate.now()))
                .hasMessageContaining("Recurrence");
        assertThatThrownBy(() -> service.generateRecurringSlots(1L, "DAILY", null))
                .hasMessageContaining("Start date");
    }

    @Test
    void bulkRetrievalAndDeleteUseRepository() {
        AvailabilitySlot slot = slot(9L, false, false);
        when(repository.saveAll(anyList())).thenReturn(List.of(slot));
        when(repository.findByProviderId(1L)).thenReturn(List.of(slot));
        when(repository.findByProviderIdAndDateAndIsBlockedFalseAndIsBookedFalse(1L, slot.getDate()))
                .thenReturn(List.of(slot));
        when(repository.findBySlotId(9L)).thenReturn(Optional.of(slot));
        when(repository.existsById(9L)).thenReturn(true);

        assertThat(service.addBulkSlots(List.of(slot))).hasSize(1);
        assertThat(service.getSlotsByProvider(1L)).hasSize(1);
        assertThat(service.getAvailabilitySlots(1L, slot.getDate())).hasSize(1);
        assertThat(service.getSlotById(9L)).isPresent();
        service.deleteSlot(9L);
        verify(repository).deleteById(9L);
    }

    @Test
    void updateBlockAndUnblockMutateSlot() {
        AvailabilitySlot existing = slot(9L, false, false);
        AvailabilitySlot update = slot(10L, false, false);
        update.setRecurrence("WEEKLY");
        when(repository.findBySlotId(9L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        AvailabilitySlot updated = service.updateSlot(9L, update);
        assertThat(updated.getRecurrence()).isEqualTo("WEEKLY");

        service.blockSlot(9L);
        assertThat(existing.getIsBlocked()).isTrue();

        service.unblockSlot(9L);
        assertThat(existing.getIsBlocked()).isFalse();
        assertThat(existing.getIsBooked()).isFalse();
    }

    @Test
    void recurringSlotsSupportsWeeklyAndRejectsUnsupportedTypes() {
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.generateRecurringSlots(3L, "WEEKLY", LocalDate.of(2026, 5, 11))).hasSize(4);

        assertThatThrownBy(() -> service.generateRecurringSlots(3L, "MONTHLY", LocalDate.now()))
                .hasMessageContaining("Unsupported");
    }

    private AvailabilitySlot slot(Long id, boolean booked, boolean blocked) {
        return AvailabilitySlot.builder()
                .slotId(id)
                .providerId(1L)
                .date(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .durationMinutes(30)
                .isBooked(booked)
                .isBlocked(blocked)
                .build();
    }
}
