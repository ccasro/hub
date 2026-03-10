package com.ccasro.hub.shared.infrastructure.api;

import com.ccasro.hub.shared.domain.ports.CityReadPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cities", description = "Cities List")
public class SharedController {

  private final CityReadPort cityReadPort;

  @GetMapping("/api/cities")
  public ResponseEntity<List<CityReadPort.CityDto>> cities(
      @RequestParam(defaultValue = "ES") String countryCode) {
    return ResponseEntity.ok(cityReadPort.findByCountry(countryCode));
  }
}
