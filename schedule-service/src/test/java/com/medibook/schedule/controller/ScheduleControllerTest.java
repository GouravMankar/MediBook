package com.medibook.schedule.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.service.ScheduleService;

@WebMvcTest(controllers = ScheduleController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    void addSlotReturnsCreatedSlot() throws Exception {
        when(scheduleService.addSlot(any(AvailabilitySlot.class))).thenReturn(slot());

        mockMvc.perform(post("/slots/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(slotJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotId").value(10));
    }

    @Test
    void addBulkSlotsReturnsCreatedSlots() throws Exception {
        when(scheduleService.addBulkSlots(anyList())).thenReturn(List.of(slot()));

        mockMvc.perform(post("/slots/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" + slotJson() + "]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slotId").value(10));
    }

    @Test
    void getSlotsByProviderReturnsSlots() throws Exception {
        when(scheduleService.getSlotsByProvider(1L)).thenReturn(List.of(slot()));

        mockMvc.perform(get("/slots/provider/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value(1));
    }

    @Test
    void getAvailabilitySlotsReturnsSlotsForDate() throws Exception {
        when(scheduleService.getAvailabilitySlots(1L, LocalDate.of(2026, 5, 20)))
                .thenReturn(List.of(slot()));

        mockMvc.perform(get("/slots/available/1").param("date", "2026-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slotId").value(10));
    }

    @Test
    void getSlotByIdReturnsSlotWhenPresent() throws Exception {
        when(scheduleService.getSlotById(10L)).thenReturn(Optional.of(slot()));

        mockMvc.perform(get("/slots/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotId").value(10));
    }

    @Test
    void getSlotByIdReturnsNotFoundWhenMissing() throws Exception {
        when(scheduleService.getSlotById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/slots/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookSlotReturnsSuccessMessage() throws Exception {
        mockMvc.perform(put("/slots/10/book"))
                .andExpect(status().isOk())
                .andExpect(content().string("Slot booked successfully"));
    }

    @Test
    void unblockSlotReturnsSuccessMessage() throws Exception {
        mockMvc.perform(put("/slots/10/unblock"))
                .andExpect(status().isOk())
                .andExpect(content().string("Slot unblocked successfully"));

        verify(scheduleService).unblockSlot(10L);
    }

    @Test
    void deleteSlotReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/slots/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Slot deleted successfully"));

        verify(scheduleService).deleteSlot(10L);
    }

    @Test
    void updateSlotReturnsUpdatedSlot() throws Exception {
        when(scheduleService.updateSlot(any(Long.class), any(AvailabilitySlot.class))).thenReturn(slot());

        mockMvc.perform(put("/slots/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(slotJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotId").value(10));
    }

    @Test
    void blockSlotReturnsSuccessMessage() throws Exception {
        mockMvc.perform(put("/slots/10/block"))
                .andExpect(status().isOk())
                .andExpect(content().string("Slot blocked successfully"));

        verify(scheduleService).blockSlot(10L);
    }

    @Test
    void generateRecurringSlotsReturnsGeneratedSlots() throws Exception {
        when(scheduleService.generateRecurringSlots(1L, "DAILY", LocalDate.of(2026, 5, 20)))
                .thenReturn(List.of(slot()));

        mockMvc.perform(post("/slots/recurring/1")
                        .param("recurrence", "DAILY")
                        .param("date", "2026-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slotId").value(10));
    }

    private String slotJson() {
        return """
                {
                  "slotId": 10,
                  "providerId": 1,
                  "date": "2026-05-20",
                  "startTime": "10:00:00",
                  "endTime": "10:30:00",
                  "durationMinutes": 30,
                  "isBooked": false,
                  "isBlocked": false
                }
                """;
    }

    private AvailabilitySlot slot() {
        return AvailabilitySlot.builder()
                .slotId(10L)
                .providerId(1L)
                .date(LocalDate.of(2026, 5, 20))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .durationMinutes(30)
                .isBooked(false)
                .isBlocked(false)
                .build();
    }
}
