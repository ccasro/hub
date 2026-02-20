package com.ccasro.hub.modules.resource.domain.valueobjects;

import java.util.Set;

public record SlotDuration(int minutes) {
  private static final Set<Integer> ALLOWED = Set.of(60, 90, 120);

  public SlotDuration {
    if (!ALLOWED.contains(minutes))
      throw new IllegalArgumentException(
          "SlotDuration must be 60, 90 or 120 minutes, received: " + minutes);
  }
}
