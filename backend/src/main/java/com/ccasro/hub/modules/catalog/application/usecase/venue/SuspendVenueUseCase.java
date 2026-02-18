package com.ccasro.hub.modules.catalog.application.usecase.venue;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspendVenueUseCase {

  private final VenueRepositoryPort venues;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public void suspend(VenueId venueId) {

    var callerId = currentUser.getUserId();

    var venue =
        venues
            .findById(venueId)
            .orElseThrow(() -> new NotFoundException("Venue not found: " + venueId));

    ownership.assertOwner(callerId, venue);

    venue.suspend();
    venues.save(venue);
  }
}
