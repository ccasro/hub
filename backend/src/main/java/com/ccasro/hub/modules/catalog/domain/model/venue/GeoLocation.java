package com.ccasro.hub.modules.catalog.domain.model.venue;

import java.math.BigDecimal;

public record GeoLocation(BigDecimal latitude, BigDecimal longitude) {
  public GeoLocation {
    if (latitude == null || longitude == null)
      throw new IllegalArgumentException("lat/lng required");
    if (latitude.compareTo(new BigDecimal("-90")) < 0
        || latitude.compareTo(new BigDecimal("90")) > 0)
      throw new IllegalArgumentException("latitude out of range");
    if (longitude.compareTo(new BigDecimal("-180")) < 0
        || longitude.compareTo(new BigDecimal("180")) > 0)
      throw new IllegalArgumentException("longitude out of range");
  }
}
