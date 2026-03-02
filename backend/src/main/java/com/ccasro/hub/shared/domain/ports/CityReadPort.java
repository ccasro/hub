package com.ccasro.hub.shared.domain.ports;

import java.util.List;

public interface CityReadPort {

  record CityDto(Long id, String name, String countryCode, double latitude, double longitude) {}

  List<CityDto> findByCountry(String countryCode);
}
