package com.medibook.provider.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.medibook.provider.dto.ProviderRequestDTO;
import com.medibook.provider.dto.ProviderResponseDTO;
import com.medibook.provider.service.ProviderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
@Slf4j
public class ProviderController {

    private final ProviderService service;

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<ProviderResponseDTO> registerProvider(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProviderRequestDTO request) {

        return ResponseEntity.ok(service.registerProvider(authHeader, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> getProviderById(
            @PathVariable Long id) {

        Optional<ProviderResponseDTO> provider = service.getProviderById(id);

        return provider
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProviderResponseDTO> getProviderByUserId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long userId) {

        ProviderResponseDTO provider = service.getProviderByUserId(authHeader, userId);

        return ResponseEntity.ok(provider);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody ProviderRequestDTO request) {

        return ResponseEntity.ok(service.updateProvider(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProvider(@PathVariable Long id) {

        service.deleteProvide(id);

        return ResponseEntity.ok("Provider deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> verifyProvider(@PathVariable Long id) {

        service.verifyProvider(id);

        return ResponseEntity.ok("Provider verified successfully");
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PutMapping("/{id}/availability")
    public ResponseEntity<String> setAvailability(
            @PathVariable Long id,
            @RequestParam Boolean status) {

        service.setAvailability(id, status);

        return ResponseEntity.ok("Availability updated successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/rating")
    public ResponseEntity<String> updateRating(
            @PathVariable Long id,
            @RequestParam Double rating) {

        service.updateRating(id, rating);

        return ResponseEntity.ok("Rating updated successfully");
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<ProviderResponseDTO>> getBySpecialization(
            @PathVariable String specialization) {

        return ResponseEntity.ok(service.getBySpecialization(specialization));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProviderResponseDTO>> searchProviders(
            @RequestParam String keyword) {

        return ResponseEntity.ok(service.searchProviders(keyword));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {

        return ResponseEntity.ok(service.getAllProviders());
    }
}
