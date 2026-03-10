package com.ccasro.hub.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "matching")
@Component
@Getter
@Setter
public class MatchingProperties {

  private int maxConcurrentMatches = 2;
  private int minHoursBeforeCreation = 48;
  private int matchExpirationHoursBefore = 24;
  private Duration organizerPaymentWindow = Duration.ofMinutes(30);
  private Duration cancellationCooldown = Duration.ofHours(24);
  private Duration noShowDetectionWindow = Duration.ofMinutes(90);
  private Duration noShowDetectionJobDelay = Duration.ofMinutes(15);
  private int leaveMatchMinHoursBefore = 48;

  private CheckIn checkIn = new CheckIn();

  @Getter
  @Setter
  public static class CheckIn {
    private double radiusKm = 0.2;
    private int windowBeforeMinutes = 30;
    private int windowAfterMinutes = 30;
    private double maxGpsAccuracyMeters = 100.0;
  }
}
