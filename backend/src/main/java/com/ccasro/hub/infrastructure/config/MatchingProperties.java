package com.ccasro.hub.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "matching")
@Getter
@Setter
public class MatchingProperties {

  private int maxConcurrentMatches = 2;
  private Duration minHoursBeforeCreation = Duration.ofHours(48);
  private Duration matchExpirationHoursBefore = Duration.ofHours(24);
  private Duration organizerPaymentWindow = Duration.ofMinutes(30);
  private Duration cancellationCooldown = Duration.ofHours(24);
  private Duration noShowDetectionWindow = Duration.ofMinutes(90);
  private Duration noShowDetectionJobDelay = Duration.ofMinutes(15);
  private Duration leaveMatchMinHoursBefore = Duration.ofHours(48);

  private CheckIn checkIn = new CheckIn();

  @Getter
  @Setter
  public static class CheckIn {
    private double radiusKm = 0.2;
    private Duration windowBeforeMinutes = Duration.ofMinutes(30);
    private Duration windowAfterMinutes = Duration.ofMinutes(30);
    private double maxGpsAccuracyMeters = 100.0;
  }
}
