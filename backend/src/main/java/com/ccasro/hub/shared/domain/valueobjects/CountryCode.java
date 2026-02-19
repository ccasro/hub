package com.ccasro.hub.shared.domain.valueobjects;

import java.util.Locale;
import java.util.Set;

public record CountryCode(String value) {
  private static final Set<String> VALID_CODES = Set.of(Locale.getISOCountries());

  public CountryCode {
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("CountryCode cannot be blank");
    value = value.toUpperCase().trim();
    if (!VALID_CODES.contains(value))
      throw new IllegalArgumentException("Invalid ISO country code: " + value);
  }
}
