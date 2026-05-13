package com.medibook.review.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReviewExceptionTest {

    @Test
    void badRequestExceptionStoresMessage() {
        assertThat(new BadRequestException("bad")).hasMessage("bad");
    }

    @Test
    void resourceNotFoundExceptionStoresMessage() {
        assertThat(new ResourceNotFoundException("missing")).hasMessage("missing");
    }
}
