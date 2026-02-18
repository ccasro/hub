package com.ccasro.hub.modules.catalog.domain.model.media;

import java.net.URI;

public record MediaUrl(URI value) {
  public static MediaUrl of(String raw) {
    if (raw == null) throw new IllegalArgumentException("url is required");
    var v = raw.trim();
    if (v.isBlank()) throw new IllegalArgumentException("url cannot be blank");
    return new MediaUrl(URI.create(v));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
