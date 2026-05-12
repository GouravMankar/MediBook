package com.medibook.appointment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.appointment.dto.AppointmentRequestDTO;
import com.medibook.appointment.dto.AppointmentResponseDTO;
import com.medibook.appointment.service.AppointmentService;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService service;

    @Test
    void bookingValidationErrorsReturnBadRequest() throws Exception {
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelReturnsUpdatedAppointmentObject() throws Exception {
        when(service.cancelAppointment(1L)).thenReturn(response("CANCELLED"));

        mockMvc.perform(put("/appointments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getByIdReturnsAppointmentWhenPresent() throws Exception {
        when(service.getById(1L)).thenReturn(Optional.of(response("SCHEDULED")));

        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1));
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() throws Exception {
        when(service.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/appointments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patientAndProviderQueriesReturnAppointments() throws Exception {
        when(service.getByPatient(2L)).thenReturn(List.of(response("SCHEDULED")));
        when(service.getByProvider(3L)).thenReturn(List.of(response("SCHEDULED")));
        when(service.getByProviderAndDate(3L, LocalDate.of(2026, 5, 20))).thenReturn(List.of(response("SCHEDULED")));

        mockMvc.perform(get("/appointments/patient/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(2));

        mockMvc.perform(get("/appointments/provider/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value(3));

        mockMvc.perform(get("/appointments/provider/3/date").param("date", "2026-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value(3));
    }

    @Test
    void bookingProtectedEndpointReturnsCreatedPayloadWhenServiceAcceptsRequest() throws Exception {
        when(service.bookAppointment(any(AppointmentRequestDTO.class))).thenReturn(response("SCHEDULED"));

        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientId": 2,
                                  "providerId": 3,
                                  "slotId": 10,
                                  "appointmentDate": "2026-05-20",
                                  "startTime": "10:00:00",
                                  "endTime": "10:30:00",
                                  "modeOfConsultation": "IN_PERSON"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void rescheduleCompleteAndStatusEndpointsReturnUpdatedAppointment() throws Exception {
        when(service.rescheduleAppointment(any(Long.class), any(AppointmentRequestDTO.class))).thenReturn(response("RESCHEDULED"));
        when(service.completeAppointment(1L)).thenReturn(response("COMPLETED"));
        when(service.updateStatus(1L, "NO_SHOW")).thenReturn(response("NO_SHOW"));

        mockMvc.perform(put("/appointments/1/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAppointmentJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESCHEDULED"));

        mockMvc.perform(put("/appointments/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(put("/appointments/1/status").param("status", "NO_SHOW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }

    @Test
    void upcomingAndCountEndpointsReturnValues() throws Exception {
        when(service.getUpcomingByPatient(2L)).thenReturn(List.of(response("SCHEDULED")));
        when(service.getAppointmentCount(3L)).thenReturn(5L);

        mockMvc.perform(get("/appointments/patient/2/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));

        mockMvc.perform(get("/appointments/count/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    private String validAppointmentJson() {
        return """
                {
                  "patientId": 2,
                  "providerId": 3,
                  "slotId": 10,
                  "appointmentDate": "2026-05-20",
                  "startTime": "10:00:00",
                  "endTime": "10:30:00",
                  "modeOfConsultation": "IN_PERSON"
                }
                """;
    }

    private AppointmentResponseDTO response(String status) {
        return AppointmentResponseDTO.builder()
                .appointmentId(1L)
                .patientId(2L)
                .providerId(3L)
                .slotId(10L)
                .appointmentDate(LocalDate.of(2026, 5, 20))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .modeOfConsultation("IN_PERSON")
                .status(status)
                .build();
    }
}
