package com.ccasro.hub.modules.matching.infrastructure.persistence;

public interface EligiblePlayerProjection {
  String getId();

  String getEmail();

  String getDisplayName();

  String getSkillLevel();

  double getCityLat();

  double getCityLng();

  boolean isMatchNotificationsEnabled();
}
