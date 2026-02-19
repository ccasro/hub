package com.ccasro.hub.modules.iam.application.dto;

public record AuthPrincipal(String sub) {
  public AuthPrincipal {
    if (sub == null || sub.isBlank()) throw new IllegalArgumentException("sub cannot be blank");
  }
}
