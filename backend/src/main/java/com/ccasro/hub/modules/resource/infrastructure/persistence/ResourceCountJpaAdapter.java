package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.ports.out.ResourceCountPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import com.ccasro.hub.modules.resource.infrastructure.persistence.projection.VenueIdCountProjection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceCountJpaAdapter implements ResourceCountPort {

  private final ResourceJpaRepository resourceJpaRepository;

  @Override
  public Map<UUID, Integer> countActiveByVenueIds(List<UUID> venueIds) {
    if (venueIds == null || venueIds.isEmpty()) return Map.of();

    return resourceJpaRepository.countByVenueIdsAndStatus(venueIds, ResourceStatus.ACTIVE).stream()
        .collect(Collectors.toMap(VenueIdCountProjection::getVenueId, p -> (int) p.getCnt()));
  }
}
