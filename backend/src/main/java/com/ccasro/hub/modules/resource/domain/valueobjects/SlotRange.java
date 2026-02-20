package com.ccasro.hub.modules.resource.domain.valueobjects;

import java.time.LocalTime;
import java.util.Objects;

public record SlotRange(LocalTime startTime, LocalTime endTime) {
  public SlotRange {
    Objects.requireNonNull(startTime, "startTime required");
    Objects.requireNonNull(endTime, "endTime required");
    if (!endTime.isAfter(startTime))
      throw new IllegalArgumentException("endTime must be later than startTime");
  }

  public boolean overlapsWith(SlotRange other) {
    return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
  }

  public boolean contains(LocalTime time) {
    return !time.isBefore(startTime) && time.isBefore(endTime);
  }
}
