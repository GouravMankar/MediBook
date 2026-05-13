package com.medibook.record.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RecordExceptionTest {

    @Test
    void badRequestExceptionStoresMessage() {
        BadRequestException exception = new BadRequestException("bad request");

        assertThat(exception).hasMessage("bad request");
    }

    @Test
    void resourceNotFoundExceptionStoresMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("missing record");

        assertThat(exception).hasMessage("missing record");
    }
}
