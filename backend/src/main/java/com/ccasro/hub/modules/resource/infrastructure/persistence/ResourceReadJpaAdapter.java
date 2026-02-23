package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.resource.infrastructure.persistence.projection.ResourceLiteProjection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceReadJpaAdapter implements ResourceReadPort {

  private final ResourceJpaRepository resourceJpaRepository;

  @Override
  public Map<UUID, ResourceLite> findLiteByIds(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) return Map.of();

    return resourceJpaRepository.findLiteByIds(ids).stream()
        .collect(
            Collectors.toMap(
                ResourceLiteProjection::getId,
                p -> new ResourceLite(p.getId(), p.getName(), p.getVenueId())));
  }
}
