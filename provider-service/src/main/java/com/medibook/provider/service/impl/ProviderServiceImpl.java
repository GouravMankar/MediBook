package com.medibook.provider.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medibook.provider.client.AuthClient;
import com.medibook.provider.dto.ProviderRequestDTO;
import com.medibook.provider.dto.ProviderResponseDTO;
import com.medibook.provider.dto.UserResponseDTO;
import com.medibook.provider.entity.Provider;
import com.medibook.provider.repository.ProviderRepository;
import com.medibook.provider.service.ProviderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final AuthClient authClient;
    private final ProviderRepository repository;

    @Override
    public ProviderResponseDTO registerProvider(String authHeader, ProviderRequestDTO request) {

        if (request.getUserId() == null) {
            throw new RuntimeException("UserId is required");
        }

        UserResponseDTO user = authClient.getUserById(authHeader, request.getUserId());

        if (user == null) {
            throw new RuntimeException("User not found. Please register first.");
        }

        if (repository.findByUserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("Provider profile already exists. Please login.");
        }

        Provider provider = new Provider();
        provider.setUserId(request.getUserId());
        provider.setSpecialization(request.getSpecialization());
        provider.setQualification(request.getQualification());
        provider.setExperienceYears(request.getExperienceYears());
        provider.setBio(request.getBio());
        provider.setClinicName(request.getClinicName());
        provider.setClinicAddress(request.getClinicAddress());
        provider.setConsultationFee(resolveFee(request));

        Provider saved = repository.save(provider);

        return mapToDTOWithUser(saved, authHeader);
    }

    @Override
    public Optional<ProviderResponseDTO> getProviderById(Long id) {

        return repository.findById(id)
                .map(provider -> mapToDTOWithUser(provider, null));
    }

    @Override
    public ProviderResponseDTO getProviderByUserId(String authHeader, Long userId) {

        Provider provider = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Provider not found for this user"));

        return mapToDTOWithUser(provider, authHeader);
    }

    @Override
    public ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO request) {

        Provider provider = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setSpecialization(request.getSpecialization());
        provider.setQualification(request.getQualification());
        provider.setExperienceYears(request.getExperienceYears());
        provider.setBio(request.getBio());
        provider.setClinicName(request.getClinicName());
        provider.setClinicAddress(request.getClinicAddress());
        provider.setConsultationFee(resolveFee(request));

        Provider updated = repository.save(provider);

        return mapToDTO(updated);
    }

    @Override
    public void verifyProvider(Long id) {

        Provider provider = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setIsVerified(true);

        repository.save(provider);
    }

    @Override
    public void setAvailability(Long id, Boolean status) {

        Provider provider = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setIsAvailable(status);

        repository.save(provider);
    }

    @Override
    public void deleteProvide(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Provider not found");
        }

        repository.deleteById(id);
    }

    @Override
    public void updateRating(Long id, Double rating) {

        Provider provider = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        Double current = provider.getAvgRating() == null ? 0.0 : provider.getAvgRating();

        provider.setAvgRating((current + rating) / 2);

        repository.save(provider);
    }

    @Override
    public List<ProviderResponseDTO> getBySpecialization(String specialization) {

        return repository.findBySpecialization(specialization)
                .stream()
                .map(provider -> mapToDTOWithUser(provider, null))
                .toList();
    }

    @Override
    public List<ProviderResponseDTO> searchProviders(String keyword) {

        return repository.search(keyword)
                .stream()
                .map(provider -> mapToDTOWithUser(provider, null))
                .toList();
    }

    @Override
    public List<ProviderResponseDTO> getAllProviders() {

        return repository.findAll()
                .stream()
                .map(provider -> mapToDTOWithUser(provider, null))
                .toList();
    }

    private ProviderResponseDTO mapToDTO(Provider provider) {

        return ProviderResponseDTO.builder()
                .providerId(provider.getProviderId())
                .userId(provider.getUserId())
                .specialization(provider.getSpecialization())
                .qualification(provider.getQualification())
                .experienceYears(provider.getExperienceYears())
                .bio(provider.getBio())
                .clinicName(provider.getClinicName())
                .clinicAddress(provider.getClinicAddress())
                .consultationFee(provider.getConsultationFee())
                .fee(provider.getConsultationFee())
                .avgRating(provider.getAvgRating())
                .isAvailable(provider.getIsAvailable())
                .isVerified(provider.getIsVerified())

                .build();
    }

    private ProviderResponseDTO mapToDTOWithUser(Provider provider, String authHeader) {

        ProviderResponseDTO dto = mapToDTO(provider);

        try {
            UserResponseDTO user = authClient.getUserById(authHeader, provider.getUserId());

            if (user != null) {
                String providerName = user.getName() != null && !user.getName().isBlank()
                        ? user.getName()
                        : "UNKNOWN PROVIDER";

                dto.setProviderName(providerName);
            }

        } catch (Exception e) {
            log.error("Failed to fetch user details for userId {}: {}",
                    provider.getUserId(), e.getMessage());
        }

        return dto;
    }

    private Double resolveFee(ProviderRequestDTO request) {
        return request.getConsultationFee() != null
                ? request.getConsultationFee()
                : request.getFee();
    }
}
