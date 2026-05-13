package com.medibook.notification.config;

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

import com.medibook.notification.controller.NotificationController;
import com.medibook.notification.service.NotificationService;

@WebMvcTest(controllers = NotificationController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private NotificationService service;

    @Test
    void notificationsEndpointIsAccessibleWithoutAuthentication() throws Exception {
        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk());

        assertThat(securityFilterChain).isNotNull();
    }
}
