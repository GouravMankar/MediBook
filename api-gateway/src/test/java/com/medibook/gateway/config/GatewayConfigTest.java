package com.medibook.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsWebFilter;

import io.swagger.v3.oas.models.OpenAPI;

class GatewayConfigTest {

    @Test
    void swaggerConfigBuildsGatewayOpenApiMetadata() {
        OpenAPI openAPI = new SwaggerConfig().gatewayOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("MediBook API Gateway");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }

    @Test
    void corsConfigCreatesFilter() {
        CorsWebFilter filter = new CorsConfig().corsWebFilter();

        assertThat(filter).isNotNull();
    }

    @Test
    void securityConfigCreatesFilterChain() {
        SecurityWebFilterChain chain = new SecurityConfig().springSecurityFilterChain(ServerHttpSecurity.http());

        assertThat(chain).isNotNull();
    }
}
