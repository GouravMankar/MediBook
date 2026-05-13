package com.medibook.review.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class ReviewConfigTest {

    @Test
    void swaggerConfigBuildsOpenApiMetadata() {
        OpenAPI openAPI = new SwaggerConfig().reviewServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Review Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }
}
