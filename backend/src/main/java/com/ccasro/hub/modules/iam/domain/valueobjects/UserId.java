package com.ccasro.hub.modules.iam.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {

  public UserId {
    Objects.requireNonNull(value, "UserId cannot be null");
  }

  public static UserId newId() {
    return new UserId(UUID.randomUUID());
  }

  public static UserId from(UUID value) {
    return new UserId(value);
  }
}
