package com.ccasro.hub.shared.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityJpaRepository extends JpaRepository<CityEntity, Long> {
  List<CityEntity> findByCountryCodeOrderByNameAsc(String countryCode);

  List<CityEntity> findByLatitudeBetweenAndLongitudeBetween(
      double latMin, double latMax, double lngMin, double lngMax);
}
