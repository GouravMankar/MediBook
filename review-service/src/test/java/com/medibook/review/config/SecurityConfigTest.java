package com.medibook.review.config;

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

import com.medibook.review.controller.ReviewController;
import com.medibook.review.service.ReviewService;

@WebMvcTest(controllers = ReviewController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private ReviewService service;

    @Test
    void reviewsEndpointIsAccessibleWithoutAuthentication() throws Exception {
        when(service.getAllReviews()).thenReturn(List.of());

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk());

        assertThat(securityFilterChain).isNotNull();
    }
}
