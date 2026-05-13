package com.medibook.notification.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.medibook.notification.dto.ErrorResponse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundReturnsNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new ResourceNotFoundException("notification not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("notification not found");
    }

    @Test
    void handleBadRequestReturnsBadRequest() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBadRequest(new BadRequestException("invalid notification"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("invalid notification");
    }

    @Test
    void handleValidationReturnsFieldMessage() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "title", "Title is required"));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(validationException(bindingResult));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Title is required");
    }

    @Test
    void handleValidationUsesDefaultMessageWhenNoFieldErrorsExist() throws Exception {
        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(validationException(new BeanPropertyBindingResult(new Object(), "request")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
    }

    @Test
    void handleGenericReturnsSafeMessage() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("secret"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Something went wrong");
    }

    private MethodArgumentNotValidException validationException(BeanPropertyBindingResult bindingResult)
            throws NoSuchMethodException {
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyEndpoint", String.class);
        return new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);
    }

    @SuppressWarnings("unused")
    private void dummyEndpoint(String value) {
    }
}
