package com.medibook.record.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.medibook.record.dto.MedicalRecordRequestDTO;
import com.medibook.record.dto.MedicalRecordResponseDTO;
import com.medibook.record.service.MedicalRecordService;

@WebMvcTest(controllers = MedicalRecordController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService service;

    @Test
    void createRecordReturnsRecord() throws Exception {
        when(service.createRecord(any(MedicalRecordRequestDTO.class))).thenReturn(record());

        mockMvc.perform(post("/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentId": 1,
                                  "patientId": 2,
                                  "providerId": 3,
                                  "diagnosis": "Fever",
                                  "prescription": "Rest"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Fever"));
    }

    @Test
    void getRecordByIdReturnsNotFoundWhenMissing() throws Exception {
        when(service.getRecordById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/records/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecordByIdReturnsRecordWhenPresent() throws Exception {
        when(service.getRecordById(1L)).thenReturn(Optional.of(record()));

        mockMvc.perform(get("/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordId").value(1));
    }

    @Test
    void getRecordByAppointmentReturnsRecordWhenPresent() throws Exception {
        when(service.getRecordByAppointment(1L)).thenReturn(Optional.of(record()));

        mockMvc.perform(get("/records/appointment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1));
    }

    @Test
    void getRecordByAppointmentReturnsNotFoundWhenMissing() throws Exception {
        when(service.getRecordByAppointment(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/records/appointment/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecordsByPatientReturnsList() throws Exception {
        when(service.getRecordsByPatient(2L)).thenReturn(List.of(record()));

        mockMvc.perform(get("/records/patient/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(2));
    }

    @Test
    void getRecordsByProviderReturnsList() throws Exception {
        when(service.getRecordsByProvider(3L)).thenReturn(List.of(record()));

        mockMvc.perform(get("/records/provider/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value(3));
    }

    @Test
    void updateRecordReturnsUpdatedRecord() throws Exception {
        when(service.updateRecord(any(Long.class), any(MedicalRecordRequestDTO.class))).thenReturn(record());

        mockMvc.perform(put("/records/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentId": 1,
                                  "patientId": 2,
                                  "providerId": 3,
                                  "diagnosis": "Fever",
                                  "prescription": "Rest"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Fever"));
    }

    @Test
    void deleteRecordReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/records/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record deleted successfully"));

        verify(service).deleteRecord(1L);
    }

    @Test
    void getAllRecordsReturnsList() throws Exception {
        when(service.getAllRecords()).thenReturn(List.of(record()));

        mockMvc.perform(get("/records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordId").value(1));
    }

    private MedicalRecordResponseDTO record() {
        return MedicalRecordResponseDTO.builder()
                .recordId(1L)
                .appointmentId(1L)
                .patientId(2L)
                .providerId(3L)
                .diagnosis("Fever")
                .prescription("Rest")
                .build();
    }
}
