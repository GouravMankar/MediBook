package com.medibook.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI scheduleServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Schedule Service API")
                        .description("APIs for managing provider availability slots")
                        .version("1.0"));
    }
}