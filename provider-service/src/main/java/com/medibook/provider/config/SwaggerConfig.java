package com.medibook.provider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI providerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Provider Service API")
                        .description("Manage doctor/provider profiles and details")
                        .version("1.0"));
    }
}