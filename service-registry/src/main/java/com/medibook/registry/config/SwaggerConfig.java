package com.medibook.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI serviceRegistryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MediBook Service Registry API")
                        .description("Eureka service discovery registry for MediBook microservices")
                        .version("1.0"));
    }
}
