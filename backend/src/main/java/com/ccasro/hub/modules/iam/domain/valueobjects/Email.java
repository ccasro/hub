package com.ccasro.hub.modules.iam.domain.valueobjects;

import java.util.regex.Pattern;

public record Email(String value) {
  private static final Pattern PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  public Email {
    if (value == null) {
      throw new IllegalArgumentException("Email is required");
    }

    value = value.trim().toLowerCase();

    if (value.isEmpty() || !PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid email: " + value);
    }
  }
}
