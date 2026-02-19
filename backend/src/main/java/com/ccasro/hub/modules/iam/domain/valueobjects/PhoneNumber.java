package com.ccasro.hub.modules.iam.domain.valueobjects;

import java.util.regex.Pattern;

public record PhoneNumber(String value) {

  private static final Pattern PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");

  public PhoneNumber {

    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("PhoneNumber must not be null or blank");
    }

    value = value.trim().replaceAll("\\s", "");

    if (!value.startsWith("+")) {
      value = "+" + value;
    }

    if (!PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid phone number format: " + value);
    }
  }
}
