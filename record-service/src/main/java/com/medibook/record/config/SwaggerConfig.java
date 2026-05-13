package com.medibook.record.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI recordServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Record Service API")
                        .description("APIs for managing patient medical records")
                        .version("1.0"));
    }
}