package com.ccasro.hub.modules.venue.application.ports.in;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.UUID;

public interface VenueAccessPolicy {
  void assertOwner(Venue venue, UserId userId);

  void assertOwner(UUID venueId, UserId userId);
}
