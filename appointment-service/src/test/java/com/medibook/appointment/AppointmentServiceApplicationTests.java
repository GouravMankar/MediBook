package com.medibook.appointment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class AppointmentServiceApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertThat(AppointmentServiceApplication.class).isNotNull();
    }

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            AppointmentServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(AppointmentServiceApplication.class, args));
        }
    }
}
