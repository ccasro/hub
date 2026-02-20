package com.ccasro.hub.modules.resource.domain.valueobjects;

public record ResourceName(String value) {
  private static final int MAX_LENGTH = 100;

  public ResourceName {
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("ResourceName required");
    value = value.trim();
    if (value.length() > MAX_LENGTH)
      throw new IllegalArgumentException("Max resourceName " + MAX_LENGTH + " chars");
  }
}
