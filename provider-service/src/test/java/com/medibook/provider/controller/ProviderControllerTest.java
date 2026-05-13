package com.medibook.provider.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.provider.dto.ProviderResponseDTO;
import com.medibook.provider.service.ProviderService;

@WebMvcTest(controllers = ProviderController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderService service;

    @Test
    void getAllProvidersReturnsProviderCards() throws Exception {
        when(service.getAllProviders()).thenReturn(List.of(provider()));

        mockMvc.perform(get("/providers/getall"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerName").value("Dr Meera Shah"))
                .andExpect(jsonPath("$[0].avgRating").value(4.5));
    }

    @Test
    void getProviderByIdReturnsNotFoundWhenMissing() throws Exception {
        when(service.getProviderById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/providers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void setAvailabilityReturnsMessage() throws Exception {
        mockMvc.perform(put("/providers/1/availability").param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Availability updated successfully"));
    }

    private ProviderResponseDTO provider() {
        return ProviderResponseDTO.builder()
                .providerId(1L)
                .userId(2L)
                .providerName("Dr Meera Shah")
                .specialization("Cardiologist")
                .avgRating(4.5)
                .consultationFee(700.0)
                .build();
    }
}
