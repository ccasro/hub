package com.ccasro.hub.modules.resource.domain.ports.out;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ResourceCountPort {
  Map<UUID, Integer> countActiveByVenueIds(List<UUID> venueIds);
}
