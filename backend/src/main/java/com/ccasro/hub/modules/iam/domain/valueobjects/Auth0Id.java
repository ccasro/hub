package com.ccasro.hub.modules.iam.domain.valueobjects;

public record Auth0Id(String value) {
  public Auth0Id {
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("Auth0Id can not be blank.");
  }
}
