package com.medibook.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.medibook.gateway.config.JwtService;

import reactor.core.publisher.Mono;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = org.mockito.Mockito.mock(JwtService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

    @Test
    void swaggerDocsArePublicWithoutJwt() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/notifications/v3/api-docs", null);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        verify(jwtService, never()).isTokenValid(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void publicProviderSearchIsAllowedForGuests() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/providers/search?keyword=cardio", null);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void authAndActuatorPathsArePublicWithoutJwt() {
        assertPublic("/auth/login");
        assertPublic("/oauth2/authorization/google");
        assertPublic("/login/oauth2/code/google");
        assertPublic("/actuator/health");
        assertPublic("/swagger-ui/index.html");
        assertPublic("/v3/api-docs");
    }

    @Test
    void publicBrowsingGetPathsAreAllowedForGuests() {
        assertPublic("/providers/getall");
        assertPublic("/providers/specialization/Cardiology");
        assertPublic("/providers/42");
        assertPublic("/slots/provider/42");
        assertPublic("/slots/available/42");
        assertPublic("/reviews/provider/42");
    }

    @Test
    void protectedPathWithoutBearerTokenReturnsUnauthorized() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/appointments", null);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isFalse();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedPathWithNonBearerAuthorizationReturnsUnauthorized() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/appointments", "Basic credentials");

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isFalse();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedPathWithInvalidTokenReturnsUnauthorized() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/appointments", "Bearer bad-token");

        when(jwtService.isTokenValid("bad-token")).thenReturn(false);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isFalse();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void providerCannotPostPayment() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/payments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer provider-token")
                        .build());

        when(jwtService.isTokenValid("provider-token")).thenReturn(true);
        when(jwtService.extractUsername("provider-token")).thenReturn("provider@example.com");
        when(jwtService.extractRole("provider-token")).thenReturn("PROVIDER");
        when(jwtService.extractUserId("provider-token")).thenReturn(7L);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isFalse();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void providerCanReadPaymentsButCannotMutateThem() {
        assertAuthorized(HttpMethod.GET, "/payments/provider/7", "provider-token", "PROVIDER", 7L);
        assertForbidden(HttpMethod.PUT, "/payments/1", "provider-token", "PROVIDER", 7L);
    }

    @Test
    void nonPatientProviderRoleCannotAccessRecordsOrReports() {
        assertForbidden(HttpMethod.GET, "/records/patient/7", "guest-token", "GUEST", 7L);
        assertForbidden(HttpMethod.GET, "/reports/patient/7", "guest-token", "GUEST", 7L);
    }

    @Test
    void patientCanAccessOwnReports() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/reports/patient/7", "Bearer patient-token");

        when(jwtService.isTokenValid("patient-token")).thenReturn(true);
        when(jwtService.extractUsername("patient-token")).thenReturn("patient@example.com");
        when(jwtService.extractRole("patient-token")).thenReturn("PATIENT");
        when(jwtService.extractUserId("patient-token")).thenReturn(7L);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void patientCannotAccessAnotherPatientReports() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/reports/patient/8", "Bearer patient-token");

        when(jwtService.isTokenValid("patient-token")).thenReturn(true);
        when(jwtService.extractUsername("patient-token")).thenReturn("patient@example.com");
        when(jwtService.extractRole("patient-token")).thenReturn("PATIENT");
        when(jwtService.extractUserId("patient-token")).thenReturn(7L);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isFalse();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void patientRecordAccessIsRestrictedToOwnRecords() {
        assertAuthorized(HttpMethod.GET, "/records/patient/7", "patient-token", "PATIENT", 7L);
        assertForbidden(HttpMethod.GET, "/records/patient/8", "patient-token", "PATIENT", 7L);
    }

    @Test
    void onlyProviderCanMutateSlots() {
        assertAuthorized(HttpMethod.POST, "/slots", "provider-token", "PROVIDER", 7L);
        assertForbidden(HttpMethod.POST, "/slots", "patient-token", "PATIENT", 7L);
        assertAuthorized(HttpMethod.GET, "/slots/provider/7", "patient-token", "PATIENT", 7L);
    }

    @Test
    void onlyPatientCanCreateReviews() {
        assertAuthorized(HttpMethod.POST, "/reviews", "patient-token", "PATIENT", 7L);
        assertForbidden(HttpMethod.POST, "/reviews", "provider-token", "PROVIDER", 7L);
        assertAuthorized(HttpMethod.GET, "/reviews/provider/7", "provider-token", "PROVIDER", 7L);
    }

    @Test
    void regularUserCannotAccessAdminPath() {
        assertForbidden(HttpMethod.GET, "/admin/users", "patient-token", "PATIENT", 7L);
    }

    @Test
    void adminCanAccessProtectedAdminPath() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange("/admin/users", "Bearer admin-token");

        when(jwtService.isTokenValid("admin-token")).thenReturn(true);
        when(jwtService.extractUsername("admin-token")).thenReturn("admin@example.com");
        when(jwtService.extractRole("admin-token")).thenReturn("ADMIN");
        when(jwtService.extractUserId("admin-token")).thenReturn(1L);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void getOrderRunsBeforeDefaultGatewayFilters() {
        assertThat(filter.getOrder()).isEqualTo(-1);
    }

    private void assertPublic(String path) {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange(path, null);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    private void assertAuthorized(HttpMethod method, String path, String token, String role, Long userId) {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange(method, path, "Bearer " + token);
        stubToken(token, role, userId);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    private void assertForbidden(HttpMethod method, String path, String token, String role, Long userId) {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = exchange(method, path, "Bearer " + token);
        stubToken(token, role, userId);

        filter.filter(exchange, chain(chainCalled)).block();

        assertThat(chainCalled).isFalse();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private void stubToken(String token, String role, Long userId) {
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn(role.toLowerCase() + "@example.com");
        when(jwtService.extractRole(token)).thenReturn(role);
        when(jwtService.extractUserId(token)).thenReturn(userId);
    }

    private MockServerWebExchange exchange(String path, String authorization) {
        return exchange(HttpMethod.GET, path, authorization);
    }

    private MockServerWebExchange exchange(HttpMethod method, String path, String authorization) {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.method(method, path);
        if (authorization != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authorization);
        }
        return MockServerWebExchange.from(builder.build());
    }

    private GatewayFilterChain chain(AtomicBoolean called) {
        return exchange -> {
            called.set(true);
            return Mono.empty();
        };
    }
}
