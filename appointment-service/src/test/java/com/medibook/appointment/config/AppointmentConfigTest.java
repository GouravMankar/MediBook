package com.medibook.appointment.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class AppointmentConfigTest {

    @Test
    void swaggerConfigBuildsOpenApiMetadata() {
        OpenAPI openAPI = new SwaggerConfig().appointmentServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Appointment Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }
}
