package com.ccasro.hub.modules.resource.infrastructure.config;

import com.ccasro.hub.modules.resource.domain.ports.out.BookedSlotsPort;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceAvailabilityConfig {

  @Bean
  public BookedSlotsPort bookedSlotsPort() {
    return (resourceId, date) -> List.of();
  }
}
