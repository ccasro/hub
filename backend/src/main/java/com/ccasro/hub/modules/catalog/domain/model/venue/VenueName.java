package com.ccasro.hub.modules.catalog.domain.model.venue;

import com.ccasro.hub.common.domain.model.TextConstraints;

public record VenueName(String value) {
  public VenueName {
    value = TextConstraints.requiredMax(value, "venue name", 80);
  }
}
