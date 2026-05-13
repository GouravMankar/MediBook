package com.medibook.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.medibook.auth.entity.User;
import com.medibook.auth.repository.UserRepository;
import com.medibook.auth.service.serviceimpl.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repository;

    @Test
    void loadsActiveUserWithRoleAuthority() {
        User user = user(true);
        when(repository.findByEmail("doctor@example.com")).thenReturn(Optional.of(user));
        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        var details = service.loadUserByUsername("doctor@example.com");

        assertThat(details.getUsername()).isEqualTo("doctor@example.com");
        assertThat(details.getAuthorities()).extracting("authority").contains("ROLE_PROVIDER");
    }

    @Test
    void rejectsMissingOrInactiveUsers() {
        CustomUserDetailsService service = new CustomUserDetailsService(repository);
        when(repository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        when(repository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user(false)));

        assertThatThrownBy(() -> service.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
        assertThatThrownBy(() -> service.loadUserByUsername("inactive@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("inactive");
    }

    private User user(boolean active) {
        User user = new User();
        user.setEmail(active ? "doctor@example.com" : "inactive@example.com");
        user.setPassword("encoded");
        user.setRole("PROVIDER");
        user.setIsActive(active);
        return user;
    }
}
