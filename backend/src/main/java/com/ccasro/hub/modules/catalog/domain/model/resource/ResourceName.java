package com.ccasro.hub.modules.catalog.domain.model.resource;

import com.ccasro.hub.common.domain.model.TextConstraints;

public record ResourceName(String value) {
  public ResourceName {
    value = TextConstraints.requiredMax(value, "resource name", 60);
  }
}
