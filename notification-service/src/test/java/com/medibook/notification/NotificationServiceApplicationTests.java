package com.medibook.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class NotificationServiceApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertThat(NotificationServiceApplication.class).isNotNull();
    }

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            NotificationServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(NotificationServiceApplication.class, args));
        }
    }
}
