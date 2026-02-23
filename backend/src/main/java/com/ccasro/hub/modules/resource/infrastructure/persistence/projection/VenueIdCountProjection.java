package com.ccasro.hub.modules.resource.infrastructure.persistence.projection;

import java.util.UUID;

public interface VenueIdCountProjection {
  UUID getVenueId();

  long getCnt();
}
