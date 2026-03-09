package com.ccasro.hub.modules.matching.domain.ports.out;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ResourceInfoPort {

  record ResourceInfo(String resourceName, String venueName, String venueCity) {}

  Optional<ResourceInfo> findByResourceId(UUID resourceId);

  Map<UUID, ResourceInfo> findByResourceIds(Set<UUID> resourceIds);
}
