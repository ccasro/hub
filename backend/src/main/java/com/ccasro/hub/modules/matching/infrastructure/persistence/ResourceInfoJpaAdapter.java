package com.ccasro.hub.modules.matching.infrastructure.persistence;

import com.ccasro.hub.modules.matching.domain.ports.out.ResourceInfoPort;
import com.ccasro.hub.modules.resource.infrastructure.persistence.ResourceJpaRepository;
import com.ccasro.hub.modules.resource.infrastructure.persistence.projection.ResourceLiteProjection;
import com.ccasro.hub.modules.venue.infrastructure.persistence.VenueJpaRepository;
import com.ccasro.hub.modules.venue.infrastructure.persistence.projection.VenueLiteProjection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceInfoJpaAdapter implements ResourceInfoPort {

  private final ResourceJpaRepository resourceRepository;
  private final VenueJpaRepository venueRepository;

  @Override
  public Optional<ResourceInfo> findByResourceId(UUID resourceId) {
    return Optional.ofNullable(findByResourceIds(Set.of(resourceId)).get(resourceId));
  }

  @Override
  public Map<UUID, ResourceInfo> findByResourceIds(Set<UUID> resourceIds) {
    if (resourceIds.isEmpty()) return Map.of();

    Map<UUID, ResourceLiteProjection> resources =
        resourceRepository.findLiteByIds(resourceIds).stream()
            .collect(Collectors.toMap(ResourceLiteProjection::getId, r -> r));

    Set<UUID> venueIds =
        resources.values().stream()
            .map(ResourceLiteProjection::getVenueId)
            .collect(Collectors.toSet());

    Map<UUID, VenueLiteProjection> venues =
        venueIds.isEmpty()
            ? Map.of()
            : venueRepository.findLiteByIds(venueIds).stream()
                .collect(Collectors.toMap(VenueLiteProjection::getId, v -> v));

    return resources.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                  ResourceLiteProjection r = e.getValue();
                  VenueLiteProjection v = venues.get(r.getVenueId());
                  return new ResourceInfo(
                      r.getName(), v != null ? v.getName() : null, v != null ? v.getCity() : null);
                }));
  }
}
