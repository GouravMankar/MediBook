package com.medibook.record.config;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.record.controller.MedicalRecordController;
import com.medibook.record.dto.MedicalRecordResponseDTO;
import com.medibook.record.service.MedicalRecordService;

@WebMvcTest(controllers = MedicalRecordController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService service;

    @Test
    void recordsEndpointIsAccessibleWithoutAuthentication() throws Exception {
        when(service.getAllRecords()).thenReturn(List.of(MedicalRecordResponseDTO.builder()
                .recordId(1L)
                .appointmentId(10L)
                .patientId(20L)
                .providerId(30L)
                .diagnosis("Flu")
                .prescription("Rest")
                .build()));

        mockMvc.perform(get("/records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordId").value(1));
    }
}
