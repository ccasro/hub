package com.ccasro.hub.modules.venue.domain.valueobjects;

public record VenueName(String value) {
  private static final int MAX_LENGTH = 150;

  public VenueName {
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("VenueName cannot be blank");
    value = value.trim();
    if (value.length() > MAX_LENGTH)
      throw new IllegalArgumentException("Max venueName " + MAX_LENGTH + " chars");
  }
}
