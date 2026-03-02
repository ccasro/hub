package com.ccasro.hub.modules.matching.domain.valueobjects;

import java.util.UUID;

public record MatchRequestId(UUID value) {
  public static MatchRequestId of(UUID value) {
    return new MatchRequestId(value);
  }

  public static MatchRequestId generate() {
    return new MatchRequestId(UUID.randomUUID());
  }
}
