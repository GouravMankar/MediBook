package com.medibook.provider.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medibook.provider.client.AuthClient;
import com.medibook.provider.dto.ProviderRequestDTO;
import com.medibook.provider.dto.ProviderResponseDTO;
import com.medibook.provider.dto.UserResponseDTO;
import com.medibook.provider.entity.Provider;
import com.medibook.provider.repository.ProviderRepository;
import com.medibook.provider.service.impl.ProviderServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProviderServiceImplTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private ProviderRepository repository;

    @InjectMocks
    private ProviderServiceImpl service;

    @Test
    void getAllProvidersAddsProviderNameFromAuthService() {
        Provider provider = Provider.builder()
                .providerId(1L)
                .userId(11L)
                .specialization("Cardiologist")
                .consultationFee(700.0)
                .avgRating(4.5)
                .build();
        UserResponseDTO user = new UserResponseDTO();
        user.setId(11L);
        user.setName("Dr Harshit Patel");

        when(repository.findAll()).thenReturn(List.of(provider));
        when(authClient.getUserById(isNull(), any(Long.class))).thenReturn(user);

        List<ProviderResponseDTO> providers = service.getAllProviders();

        assertThat(providers).hasSize(1);
        assertThat(providers.get(0).getProviderName()).isEqualTo("Dr Harshit Patel");
        assertThat(providers.get(0).getAvgRating()).isEqualTo(4.5);
    }

    @Test
    void updateProviderPersistsChangedProfileAndReturnsUpdatedDto() {
        Provider existing = Provider.builder()
                .providerId(2L)
                .userId(12L)
                .specialization("General")
                .consultationFee(300.0)
                .build();
        ProviderRequestDTO request = new ProviderRequestDTO();
        request.setSpecialization("Dermatologist");
        request.setQualification("MBBS MD");
        request.setExperienceYears(6);
        request.setClinicName("Skin Care Clinic");
        request.setConsultationFee(900.0);

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        ProviderResponseDTO updated = service.updateProvider(2L, request);

        assertThat(updated.getSpecialization()).isEqualTo("Dermatologist");
        assertThat(updated.getQualification()).isEqualTo("MBBS MD");
        assertThat(updated.getConsultationFee()).isEqualTo(900.0);
        verify(repository).save(existing);
    }

    @Test
    void registerProviderRequiresUserAndStoresProfile() {
        ProviderRequestDTO request = request();
        UserResponseDTO user = new UserResponseDTO();
        user.setId(12L);
        user.setName("Dr Asha");
        when(authClient.getUserById("Bearer token", 12L)).thenReturn(user);
        when(repository.findByUserId(12L)).thenReturn(Optional.empty());
        when(repository.save(any(Provider.class))).thenAnswer(invocation -> {
            Provider provider = invocation.getArgument(0);
            provider.setProviderId(3L);
            return provider;
        });

        ProviderResponseDTO response = service.registerProvider("Bearer token", request);

        assertThat(response.getProviderId()).isEqualTo(3L);
        assertThat(response.getProviderName()).isEqualTo("Dr Asha");
        assertThat(response.getConsultationFee()).isEqualTo(600.0);
    }

    @Test
    void registerProviderRejectsMissingUserIdAndDuplicateProfile() {
        ProviderRequestDTO missingUser = request();
        missingUser.setUserId(null);

        assertThatThrownBy(() -> service.registerProvider("token", missingUser))
                .hasMessageContaining("UserId");

        ProviderRequestDTO duplicate = request();
        when(authClient.getUserById("token", 12L)).thenReturn(new UserResponseDTO());
        when(repository.findByUserId(12L)).thenReturn(Optional.of(Provider.builder().build()));

        assertThatThrownBy(() -> service.registerProvider("token", duplicate))
                .hasMessageContaining("already exists");
    }

    @Test
    void verifyAvailabilityRatingDeleteAndSearchUseRepository() {
        Provider provider = Provider.builder().providerId(2L).userId(12L).avgRating(4.0).build();
        when(repository.findById(2L)).thenReturn(Optional.of(provider));
        when(repository.save(provider)).thenReturn(provider);

        service.verifyProvider(2L);
        assertThat(provider.getIsVerified()).isTrue();

        service.setAvailability(2L, true);
        assertThat(provider.getIsAvailable()).isTrue();

        service.updateRating(2L, 5.0);
        assertThat(provider.getAvgRating()).isEqualTo(4.5);

        when(repository.existsById(2L)).thenReturn(true);
        service.deleteProvide(2L);
        verify(repository).deleteById(2L);

        when(repository.findBySpecialization("Cardio")).thenReturn(List.of(provider));
        when(repository.search("heart")).thenReturn(List.of(provider));
        assertThat(service.getBySpecialization("Cardio")).hasSize(1);
        assertThat(service.searchProviders("heart")).hasSize(1);
    }

    @Test
    void getProviderByIdAndUserIdMapProviderName() {
        Provider provider = Provider.builder().providerId(2L).userId(12L).build();
        UserResponseDTO user = new UserResponseDTO();
        user.setId(12L);
        user.setName("Dr Asha");
        when(repository.findById(2L)).thenReturn(Optional.of(provider));
        when(repository.findByUserId(12L)).thenReturn(Optional.of(provider));
        when(authClient.getUserById(any(), any(Long.class))).thenReturn(user);

        assertThat(service.getProviderById(2L)).isPresent();
        assertThat(service.getProviderByUserId("token", 12L).getProviderName()).isEqualTo("Dr Asha");
    }

    private ProviderRequestDTO request() {
        ProviderRequestDTO request = new ProviderRequestDTO();
        request.setUserId(12L);
        request.setSpecialization("Cardio");
        request.setQualification("MBBS");
        request.setExperienceYears(5);
        request.setClinicName("Care Clinic");
        request.setClinicAddress("Main Road");
        request.setFee(600.0);
        return request;
    }
}
