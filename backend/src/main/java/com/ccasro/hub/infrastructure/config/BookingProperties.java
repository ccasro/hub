package com.ccasro.hub.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "booking")
@Component
@Getter
@Setter
public class BookingProperties {
  private Duration paymentHoldDuration = Duration.ofMinutes(5);
}
