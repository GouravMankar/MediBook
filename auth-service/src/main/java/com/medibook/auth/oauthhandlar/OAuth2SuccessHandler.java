package com.medibook.auth.oauthhandlar;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.medibook.auth.entity.User;
import com.medibook.auth.repository.UserRepository;
import com.medibook.auth.service.serviceimpl.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = token.getPrincipal();

        String provider = token.getAuthorizedClientRegistrationId().toUpperCase();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        if (email == null) {
            String login = oauthUser.getAttribute("login");
            email = login + "@github.com";
        }

        if (name == null) {
            name = oauthUser.getAttribute("login");
        }

        final String finalEmail = email;
        final String finalName = name;
        final String finalPicture = picture;
        final String finalProvider = provider;

        Optional<User> existingUser = userRepository.findByEmail(finalEmail);

        User user = existingUser.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(finalEmail);
            newUser.setName(finalName);
            newUser.setProvider(finalProvider);
            newUser.setPassword("Auth_Pass");
            newUser.setRole("PATIENT");
            newUser.setProfilePicUrl(finalPicture);
            newUser.setIsActive(true);
            return userRepository.save(newUser);
        });

        String jwt = jwtService.generateToken(user);

        response.sendRedirect(frontendBaseUrl + "/auth/callback?token=" + jwt);
    }
}
