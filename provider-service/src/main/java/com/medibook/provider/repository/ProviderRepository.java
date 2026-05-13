package com.medibook.provider.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medibook.provider.entity.Provider;


public interface ProviderRepository extends JpaRepository<Provider, Long> {

	Optional<Provider> findByUserId(Long userId);

	List<Provider> findBySpecialization(String specialization);

	List<Provider> findBySpecializationContainingIgnoreCase(String keyword);

	@Query("""
			select p from Provider p
			where lower(p.specialization) like lower(concat('%', :keyword, '%'))
			   or lower(p.clinicName) like lower(concat('%', :keyword, '%'))
			   or lower(p.clinicAddress) like lower(concat('%', :keyword, '%'))
			""")
	List<Provider> search(@Param("keyword") String keyword);

}
