package com.ccasro.hub.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "iam")
@Component
@Getter
@Setter
public class IamProperties {

  private int noShowBanThreshold = 3;
  private Duration noShowBanDuration = Duration.ofDays(30);
}
