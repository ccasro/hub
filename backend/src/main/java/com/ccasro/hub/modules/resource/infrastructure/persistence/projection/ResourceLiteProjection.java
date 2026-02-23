package com.ccasro.hub.modules.resource.infrastructure.persistence.projection;

import java.util.UUID;

public interface ResourceLiteProjection {
  UUID getId();

  String getName();

  UUID getVenueId();
}
