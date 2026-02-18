package com.ccasro.hub.modules.catalog.domain.port;

import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.Optional;

public interface VenueRepositoryPort {
  void save(Venue venue);

  Optional<Venue> findById(VenueId id);

  boolean existsByIdAndOwnerUserId(VenueId venueId, UserId ownerUserId);

  int countByOwnerUserId(UserId ownerUserId);
}
