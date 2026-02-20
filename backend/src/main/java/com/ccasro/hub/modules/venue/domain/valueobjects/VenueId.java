package com.ccasro.hub.modules.venue.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record VenueId(UUID value) {
  public VenueId {
    Objects.requireNonNull(value, "VenueId required");
  }

  public static VenueId generate() {
    return new VenueId(UUID.randomUUID());
  }

  public static VenueId of(UUID value) {
    return new VenueId(value);
  }
}
