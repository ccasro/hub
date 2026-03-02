package com.ccasro.hub.modules.venue.infrastructure.persistence;

import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.venue.infrastructure.persistence.projection.VenueLiteProjection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                p ->
                    new VenueLite(
                        p.getId(), p.getName(), p.getCity(), p.getLatitude(), p.getLongitude())));
  }

  @Override
  public List<VenueLite> findActiveVenuesNear(double lat, double lng, double radiusKm) {
    double radiusMeters = radiusKm * 1000.0;

    return venueJpaRepository.findActiveNearby(lat, lng, radiusMeters).stream()
        .map(
            v ->
                new VenueLite(
                    v.getId(),
                    v.getName(),
                    v.getCity(),
                    v.getLocation() != null ? v.getLocation().getY() : null,
                    v.getLocation() != null ? v.getLocation().getX() : null))
        .toList();
  }
}
