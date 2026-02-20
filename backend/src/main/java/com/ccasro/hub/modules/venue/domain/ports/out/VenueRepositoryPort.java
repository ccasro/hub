package com.ccasro.hub.modules.venue.domain.ports.out;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.List;
import java.util.Optional;

public interface VenueRepositoryPort {
  Venue save(Venue venue);

  Optional<Venue> findById(VenueId id);

  List<Venue> findAllActive();

  List<Venue> findByOwnerId(UserId ownerId);

  List<Venue> findAll(int page, int size);

  List<Venue> findByStatus(VenueStatus status);

  List<Venue> findActiveNearby(double lat, double lng, double radiusMeters);

  boolean existsOwnedBy(VenueId venueId, UserId ownerId);

  void deleteById(VenueId id);
}
