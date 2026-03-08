package com.ccasro.hub.shared.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityJpaRepository extends JpaRepository<CityEntity, Long> {
  List<CityEntity> findByCountryCodeOrderByNameAsc(String countryCode);

  Optional<CityEntity> findByNameIgnoreCaseAndCountryCode(String name, String countryCode);
}
