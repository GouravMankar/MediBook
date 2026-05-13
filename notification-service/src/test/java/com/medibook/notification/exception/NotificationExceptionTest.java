package com.medibook.notification.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotificationExceptionTest {

    @Test
    void badRequestExceptionStoresMessage() {
        assertThat(new BadRequestException("bad")).hasMessage("bad");
    }

    @Test
    void resourceNotFoundExceptionStoresMessage() {
        assertThat(new ResourceNotFoundException("missing")).hasMessage("missing");
    }
}
