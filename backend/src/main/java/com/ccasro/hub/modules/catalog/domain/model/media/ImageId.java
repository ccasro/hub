package com.ccasro.hub.modules.catalog.domain.model.media;

import java.util.Objects;
import java.util.UUID;

public record ImageId(UUID value) {

  public ImageId {
    Objects.requireNonNull(value);
  }

  public static ImageId newId() {
    return new ImageId(UUID.randomUUID());
  }

  public static ImageId of(String raw) {
    return new ImageId(UUID.fromString(raw));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
