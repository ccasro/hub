package com.ccasro.hub.modules.iam.domain.valueobjects;

public record DisplayName(String value) {

  private static final int MAX_LENGTH = 100;
  private static final String VALID_PATTERN = "^[\\p{L}0-9 ._'-]+$";

  public DisplayName {

    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("DisplayName must not be null or blank");
    }

    value = value.trim();

    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
          "DisplayName must not exceed " + MAX_LENGTH + " characters");
    }

    if (!value.matches(VALID_PATTERN)) {
      throw new IllegalArgumentException("DisplayName contains invalid characters");
    }
  }
}
