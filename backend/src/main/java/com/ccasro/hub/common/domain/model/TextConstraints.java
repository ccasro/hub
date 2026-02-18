package com.ccasro.hub.common.domain.model;

public final class TextConstraints {

  private TextConstraints() {}

  public static String requiredMax(String raw, String field, int maxLen) {
    if (raw == null) throw new IllegalArgumentException(field + " is required");
    String v = raw.trim();
    if (v.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
    if (v.length() > maxLen) throw new IllegalArgumentException(field + " too long");
    return v;
  }

  public static String optionalMax(String raw, String field, int maxLen) {
    if (raw == null) return null;
    String v = raw.trim();
    if (v.isBlank()) return null;
    if (v.length() > maxLen) throw new IllegalArgumentException(field + " too long");
    return v;
  }
}
