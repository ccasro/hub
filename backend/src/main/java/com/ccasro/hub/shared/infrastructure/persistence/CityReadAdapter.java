package com.ccasro.hub.shared.infrastructure.persistence;

import com.ccasro.hub.shared.domain.ports.CityReadPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityReadAdapter implements CityReadPort {

  private final CityJpaRepository cityJpaRepository;

  @Override
  @Cacheable(value = "cities", key = "#countryCode")
  public List<CityDto> findByCountry(String countryCode) {
    return cityJpaRepository.findByCountryCodeOrderByNameAsc(countryCode).stream()
        .map(
            c ->
                new CityDto(
                    c.getId(), c.getName(), c.getCountryCode(), c.getLatitude(), c.getLongitude()))
        .toList();
  }
}
