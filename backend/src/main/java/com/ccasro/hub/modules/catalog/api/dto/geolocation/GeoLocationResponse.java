package com.ccasro.hub.modules.catalog.api.dto.geolocation;

import com.ccasro.hub.modules.catalog.domain.model.venue.GeoLocation;
import java.math.BigDecimal;

public record GeoLocationResponse(BigDecimal latitude, BigDecimal longitude) {
  public static GeoLocationResponse from(GeoLocation geo) {
    return new GeoLocationResponse(geo.latitude(), geo.longitude());
  }
}
