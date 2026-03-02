package com.ccasro.hub.modules.venue.infrastructure.persistence.projection;

import java.util.UUID;

public interface VenueLiteProjection {
  UUID getId();

  String getName();

  String getCity();

  Double getLatitude();

  Double getLongitude();
}
