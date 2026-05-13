package com.medibook.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange

                        // CORS preflight
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .pathMatchers(
                                "/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/**",

                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/actuator/**",

                                "/auth/v3/api-docs",
                                "/auth/v3/api-docs/**",
                                "/providers/v3/api-docs",
                                "/providers/v3/api-docs/**",
                                "/slots/v3/api-docs",
                                "/slots/v3/api-docs/**",
                                "/appointments/v3/api-docs",
                                "/appointments/v3/api-docs/**",
                                "/payments/v3/api-docs",
                                "/payments/v3/api-docs/**",
                                "/reviews/v3/api-docs",
                                "/reviews/v3/api-docs/**",
                                "/notifications/v3/api-docs",
                                "/notifications/v3/api-docs/**",
                                "/records/v3/api-docs",
                                "/records/v3/api-docs/**",
                                "/registry/v3/api-docs",
                                "/registry/v3/api-docs/**"
                        ).permitAll()

                        .anyExchange().permitAll()
                )
                .build();
    }
}
