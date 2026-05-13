package com.medibook.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ReviewServiceApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertThat(ReviewServiceApplication.class).isNotNull();
    }

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ReviewServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ReviewServiceApplication.class, args));
        }
    }
}
