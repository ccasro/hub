package com.ccasro.hub.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "booking")
@Getter
@Setter
public class BookingProperties {
  private Duration paymentHoldDuration = Duration.ofMinutes(5);
  private Duration cancellationMinHoursBefore = Duration.ofHours(24);
}
