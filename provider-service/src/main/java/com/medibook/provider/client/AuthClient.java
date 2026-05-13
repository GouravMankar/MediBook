package com.medibook.provider.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.medibook.provider.dto.UserResponseDTO;


@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/auth/profile/{id}")
    UserResponseDTO getUserById(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id
    );
}
