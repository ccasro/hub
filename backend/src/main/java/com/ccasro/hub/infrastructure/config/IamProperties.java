package com.ccasro.hub.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iam")
@Getter
@Setter
public class IamProperties {

  private int noShowBanThreshold = 3;
  private Duration noShowBanDuration = Duration.ofDays(30);
}
