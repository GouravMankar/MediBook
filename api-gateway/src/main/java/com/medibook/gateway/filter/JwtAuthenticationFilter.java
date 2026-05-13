package com.medibook.gateway.filter;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.medibook.gateway.config.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        log.info("Gateway path: {}", path);

        if (isPublicPath(path, exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        Long userId = jwtService.extractUserId(token);

        if (!isAuthorized(path, exchange.getRequest().getMethod(), role, userId)) {
            log.warn("Role {} is forbidden for path: {}", role, path);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private boolean isPublicPath(String path, HttpMethod method) {
        return path.startsWith("/auth/")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.contains("/v3/api-docs")
                || (method == HttpMethod.GET && isPublicGetPath(path));
    }

    private boolean isPublicGetPath(String path) {
        return path.equals("/providers/getall")
                || path.startsWith("/providers/search")
                || path.startsWith("/providers/specialization/")
                || path.matches("^/providers/\\d+$")
                || path.startsWith("/slots/provider/")
                || path.startsWith("/slots/available/")
                || path.startsWith("/reviews/provider/");
    }

    private boolean isAuthorized(String path, HttpMethod method, String role, Long userId) {
        String normalizedRole = role == null ? "" : role.toUpperCase();

        if ("ADMIN".equals(normalizedRole)) {
            return true;
        }

        if (path.startsWith("/admin/")) {
            return false;
        }

        if (path.startsWith("/payments") && !"PATIENT".equals(normalizedRole)) {
            return method == HttpMethod.GET && "PROVIDER".equals(normalizedRole);
        }

        if ((path.startsWith("/records") || path.startsWith("/reports"))
                && !List.of("PATIENT", "PROVIDER").contains(normalizedRole)) {
            return false;
        }

        if (path.startsWith("/reports/patient/") && "PATIENT".equals(normalizedRole)) {
            return path.equals("/reports/patient/" + userId);
        }

        if (path.startsWith("/records/patient/") && "PATIENT".equals(normalizedRole)) {
            return path.equals("/records/patient/" + userId);
        }

        if (path.startsWith("/slots")
                && method != HttpMethod.GET
                && !"PROVIDER".equals(normalizedRole)) {
            return false;
        }

        if (path.startsWith("/reviews") && method != HttpMethod.GET
                && !"PATIENT".equals(normalizedRole)) {
            return false;
        }

        return true;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
