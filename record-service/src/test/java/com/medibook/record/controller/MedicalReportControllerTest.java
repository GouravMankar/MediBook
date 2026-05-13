package com.medibook.record.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import com.medibook.record.dto.MedicalReportRequestDTO;
import com.medibook.record.dto.MedicalReportResponseDTO;
import com.medibook.record.service.MedicalReportService;

@WebMvcTest(controllers = MedicalReportController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MedicalReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalReportService service;

    @Test
    void createReportReturnsReport() throws Exception {
        when(service.createReport(any(MedicalReportRequestDTO.class))).thenReturn(report());

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentId": 1,
                                  "patientId": 2,
                                  "providerId": 3,
                                  "diagnosis": "Migraine",
                                  "prescription": "Medication",
                                  "reportDate": "2026-05-20",
                                  "providerName": "Dr Meera"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Migraine"));
    }

    @Test
    void getReportByIdReturnsNotFoundWhenMissing() throws Exception {
        when(service.getReportById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reports/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReportByIdReturnsReportWhenPresent() throws Exception {
        when(service.getReportById(1L)).thenReturn(Optional.of(report()));

        mockMvc.perform(get("/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1));
    }

    @Test
    void getReportsByPatientReturnsList() throws Exception {
        when(service.getReportsByPatient(2L)).thenReturn(List.of(report()));

        mockMvc.perform(get("/reports/patient/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerName").value("Dr Meera"));
    }

    @Test
    void getReportsByProviderReturnsList() throws Exception {
        when(service.getReportsByProvider(3L)).thenReturn(List.of(report()));

        mockMvc.perform(get("/reports/provider/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value(3));
    }

    private MedicalReportResponseDTO report() {
        return MedicalReportResponseDTO.builder()
                .reportId(1L)
                .appointmentId(1L)
                .patientId(2L)
                .providerId(3L)
                .diagnosis("Migraine")
                .prescription("Medication")
                .reportDate(LocalDate.of(2026, 5, 20))
                .providerName("Dr Meera")
                .build();
    }
}
