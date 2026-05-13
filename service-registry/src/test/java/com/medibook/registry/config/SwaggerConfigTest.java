package com.medibook.registry.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class SwaggerConfigTest {

    @Test
    void serviceRegistryOpenApiContainsRegistryMetadata() {
        OpenAPI openAPI = new SwaggerConfig().serviceRegistryOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("MediBook Service Registry API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }
}
