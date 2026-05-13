package com.medibook.record;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class RecordServiceApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertThat(RecordServiceApplication.class).isNotNull();
    }

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            RecordServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(RecordServiceApplication.class, args));
        }
    }
}
