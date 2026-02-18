package com.ccasro.hub.modules.catalog.application.query.dto;

import com.ccasro.hub.modules.catalog.domain.model.venue.GeoLocation;
import java.math.BigDecimal;

public record GeoLocationDto(BigDecimal latitude, BigDecimal longitude) {
  public static GeoLocationDto from(GeoLocation g) {
    return new GeoLocationDto(g.latitude(), g.longitude());
  }
}
