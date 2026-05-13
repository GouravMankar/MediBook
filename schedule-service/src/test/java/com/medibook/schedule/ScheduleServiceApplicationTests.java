package com.medibook.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ScheduleServiceApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertThat(ScheduleServiceApplication.class).isNotNull();
    }

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ScheduleServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ScheduleServiceApplication.class, args));
        }
    }
}
