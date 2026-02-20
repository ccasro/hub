package com.ccasro.hub.modules.resource.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record ResourceId(UUID value) {
  public ResourceId {
    Objects.requireNonNull(value, "ResourceId required");
  }

  public static ResourceId generate() {
    return new ResourceId(UUID.randomUUID());
  }

  public static ResourceId of(UUID value) {
    return new ResourceId(value);
  }
}
