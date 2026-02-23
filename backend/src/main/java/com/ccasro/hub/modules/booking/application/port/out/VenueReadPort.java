package com.ccasro.hub.modules.booking.application.port.out;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface VenueReadPort {
  Map<UUID, VenueLite> findLiteByIds(Set<UUID> ids);

  record VenueLite(UUID id, String name, String city) {}
}
