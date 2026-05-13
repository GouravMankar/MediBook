package com.medibook.auth.oauthhandlar;

import java.io.IOException;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException {

        String errorMessage = exception.getMessage();

        String redirectUrl = "http://localhost:4200/login?error=" + errorMessage;

        response.sendRedirect(redirectUrl);
    }
}