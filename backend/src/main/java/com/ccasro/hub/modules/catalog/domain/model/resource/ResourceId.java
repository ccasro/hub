package com.ccasro.hub.modules.catalog.domain.model.resource;

import java.util.Objects;
import java.util.UUID;

public record ResourceId(UUID value) {

  public ResourceId {
    Objects.requireNonNull(value);
  }

  public static ResourceId newId() {
    return new ResourceId(UUID.randomUUID());
  }

  public static ResourceId of(String raw) {
    return new ResourceId(UUID.fromString(raw));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
