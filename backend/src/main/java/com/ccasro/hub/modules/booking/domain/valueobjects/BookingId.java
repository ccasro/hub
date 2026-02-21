package com.ccasro.hub.modules.booking.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record BookingId(UUID value) {
  public BookingId {
    Objects.requireNonNull(value, "BookingId required");
  }

  public static BookingId generate() {
    return new BookingId(UUID.randomUUID());
  }

  public static BookingId of(UUID value) {
    return new BookingId(value);
  }
}
