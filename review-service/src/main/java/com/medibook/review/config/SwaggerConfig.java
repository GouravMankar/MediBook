package com.medibook.review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI reviewServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Review Service API")
                        .description("APIs for managing appointment reviews and provider ratings")
                        .version("1.0"));
    }
}