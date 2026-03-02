package com.ccasro.hub.modules.booking.application.port.out;

import java.util.*;

public interface ResourceReadPort {
  Map<UUID, ResourceLite> findLiteByIds(Collection<UUID> ids);

  List<ResourceLite> findActiveByVenueIds(Collection<UUID> venueIds);

  record ResourceLite(UUID id, String name, UUID venueId, String type) {
    public ResourceLite(UUID id, String name, UUID venueId) {
      this(id, name, venueId, null);
    }
  }
}
