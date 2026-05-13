package com.medibook.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ApiGatewayApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertThat(ApiGatewayApplication.class).isNotNull();
    }

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ApiGatewayApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ApiGatewayApplication.class, args));
        }
    }
}
