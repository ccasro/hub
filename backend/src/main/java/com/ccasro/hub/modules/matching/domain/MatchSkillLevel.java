package com.ccasro.hub.modules.matching.domain;

public enum MatchSkillLevel {
  BEGINNER,
  INTERMEDIATE,
  ADVANCED,
  ANY;

  public boolean matches(String playerSkillLevel) {
    if (this == ANY) return true;
    if (playerSkillLevel == null) return false;
    return this.name().equalsIgnoreCase(playerSkillLevel);
  }
}
