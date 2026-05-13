package com.medibook.schedule.config;

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

import com.medibook.schedule.controller.ScheduleController;
import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.service.ScheduleService;

@WebMvcTest(controllers = ScheduleController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    void slotsEndpointIsAccessibleWithoutAuthentication() throws Exception {
        when(scheduleService.getSlotsByProvider(1L)).thenReturn(List.of(new AvailabilitySlot()));

        mockMvc.perform(get("/slots/provider/1"))
                .andExpect(status().isOk());

        assertThat(securityFilterChain).isNotNull();
    }
}
