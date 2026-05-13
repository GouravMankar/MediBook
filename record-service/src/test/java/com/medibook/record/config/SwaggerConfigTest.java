package com.medibook.record.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class SwaggerConfigTest {

    @Test
    void recordServiceOpenApiContainsRecordServiceInfo() {
        OpenAPI openAPI = new SwaggerConfig().recordServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Medical Record Service API");
        assertThat(openAPI.getInfo().getDescription()).isEqualTo("APIs for managing patient medical records");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }
}
