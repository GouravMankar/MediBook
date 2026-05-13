package com.medibook.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ServiceRegistryApplicationTest {

    @Test
    void applicationClassIsPresent() {
        assertThat(ServiceRegistryApplication.class).isNotNull();
    }

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ServiceRegistryApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ServiceRegistryApplication.class, args));
        }
    }
}
