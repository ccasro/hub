package com.ccasro.hub.modules.catalog.application.policy;

import com.ccasro.hub.common.domain.exception.ForbiddenException;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import com.ccasro.hub.modules.iam.domain.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VenueOwnershipPolicy {
  private final VenueRepositoryPort venues;

  public void assertOwner(UserId userId, Venue venue) {
    if (!venue.isOwnedBy(userId)) {
      throw new ForbiddenException("You are not the owner of this venue");
    }
  }

  public void assertOwner(UserId userId, VenueId venueId) {
    if (!venues.existsByIdAndOwnerUserId(venueId, userId)) {
      throw new ForbiddenException("You are not the owner of this venue");
    }
  }
}
