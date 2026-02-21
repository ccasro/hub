package com.ccasro.hub.modules.booking.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {
  public PaymentId {
    Objects.requireNonNull(value, "PaymentId required");
  }

  public static PaymentId generate() {
    return new PaymentId(UUID.randomUUID());
  }

  public static PaymentId of(UUID value) {
    return new PaymentId(value);
  }
}
