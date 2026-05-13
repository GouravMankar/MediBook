package com.medibook.provider.service;

import java.util.List;
import java.util.Optional;

import com.medibook.provider.dto.ProviderRequestDTO;
import com.medibook.provider.dto.ProviderResponseDTO;

public interface ProviderService {

    ProviderResponseDTO registerProvider(String authHeader, ProviderRequestDTO request);

    Optional<ProviderResponseDTO> getProviderById(Long id);

    ProviderResponseDTO getProviderByUserId(String authHeader, Long userId);

    List<ProviderResponseDTO> getBySpecialization(String specialization);

    List<ProviderResponseDTO> searchProviders(String keyword);

    ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO request);

    void verifyProvider(Long id);

    void setAvailability(Long id, Boolean status);

    void deleteProvide(Long id);

    void updateRating(Long id, Double rating);

    List<ProviderResponseDTO> getAllProviders();
}
