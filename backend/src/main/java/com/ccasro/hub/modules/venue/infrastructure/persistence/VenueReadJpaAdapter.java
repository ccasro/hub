package com.ccasro.hub.modules.venue.infrastructure.persistence;

import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.venue.infrastructure.persistence.projection.VenueLiteProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VenueReadJpaAdapter implements VenueReadPort {

  private final VenueJpaRepository venueJpaRepository;

  @Override
  public Map<UUID, VenueLite> findLiteByIds(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) return Map.of();

    return venueJpaRepository.findLiteByIds(ids).stream()
        .collect(
            Collectors.toMap(
                VenueLiteProjection::getId,
                p -> new VenueLite(p.getId(), p.getName(), p.getCity())));
  }
}
