package com.ccasro.hub.modules.resource.application.policy;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;

public interface VenueAccessPolicy {
  void assertOwner(Venue venue, UserId userId);

  void assertOwner(VenueId venueId, UserId userId);
}
