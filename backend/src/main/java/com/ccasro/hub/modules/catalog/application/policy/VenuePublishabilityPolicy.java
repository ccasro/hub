package com.ccasro.hub.modules.catalog.application.policy;

import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;

public interface VenuePublishabilityPolicy {
  void ensurePublishable(Venue venue);
}
