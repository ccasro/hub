package com.ccasro.hub.modules.catalog.application.usecase.venue;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.application.policy.VenuePublishabilityPolicy;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivateVenueUseCase {

  private final VenueRepositoryPort venues;
  private final VenuePublishabilityPolicy publishabilityPolicy;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public Venue activate(VenueId venueId) {
    var callerId = currentUser.getUserId();

    var venue =
        venues
            .findById(venueId)
            .orElseThrow(() -> new NotFoundException("Venue not found: " + venueId));

    ownership.assertOwner(callerId, venue);

    venue.ensurePublishable(publishabilityPolicy);

    venue.activate();

    venues.save(venue);
    return venue;
  }
}
