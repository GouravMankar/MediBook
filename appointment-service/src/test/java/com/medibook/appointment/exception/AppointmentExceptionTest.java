package com.medibook.appointment.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AppointmentExceptionTest {

    @Test
    void badRequestExceptionStoresMessage() {
        assertThat(new BadRequestException("bad")).hasMessage("bad");
    }

    @Test
    void resourceNotFoundExceptionStoresMessage() {
        assertThat(new ResourceNotFoundException("missing")).hasMessage("missing");
    }
}
