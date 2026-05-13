package com.medibook.appointment.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.appointment.controller.AppointmentController;
import com.medibook.appointment.service.AppointmentService;

@WebMvcTest(controllers = AppointmentController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private AppointmentService service;

    @Test
    void appointmentEndpointIsAccessibleWithoutAuthentication() throws Exception {
        when(service.getByPatient(1L)).thenReturn(List.of());

        mockMvc.perform(get("/appointments/patient/1"))
                .andExpect(status().isOk());

        assertThat(securityFilterChain).isNotNull();
    }
}
