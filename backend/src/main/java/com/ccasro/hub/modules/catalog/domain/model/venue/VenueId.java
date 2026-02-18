package com.ccasro.hub.modules.catalog.domain.model.venue;

import java.util.Objects;
import java.util.UUID;

public record VenueId(UUID value) {

  public VenueId {
    Objects.requireNonNull(value);
  }

  public static VenueId newId() {
    return new VenueId(UUID.randomUUID());
  }

  public static VenueId of(String raw) {
    return new VenueId(UUID.fromString(raw));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
