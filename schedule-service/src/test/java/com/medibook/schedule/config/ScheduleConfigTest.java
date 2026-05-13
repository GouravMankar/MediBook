package com.medibook.schedule.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class ScheduleConfigTest {

    @Test
    void swaggerConfigBuildsOpenApiMetadata() {
        OpenAPI openAPI = new SwaggerConfig().scheduleServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Schedule Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }

}
