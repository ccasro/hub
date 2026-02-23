package com.ccasro.hub.modules.booking.application.port.out;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ResourceReadPort {
  Map<UUID, ResourceLite> findLiteByIds(Set<UUID> ids);

  record ResourceLite(UUID id, String name, UUID venueId) {}
}
