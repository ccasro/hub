package com.ccasro.hub.modules.catalog.domain.model.media;

import com.ccasro.hub.common.domain.model.TextConstraints;

public record MediaPublicId(String value) {
  public MediaPublicId {
    value = TextConstraints.requiredMax(value, "publicId", 500);
  }
}
