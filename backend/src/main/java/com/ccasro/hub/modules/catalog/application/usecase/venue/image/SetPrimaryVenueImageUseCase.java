package com.ccasro.hub.modules.catalog.application.usecase.venue.image;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SetPrimaryVenueImageUseCase {

  private final VenueRepositoryPort venues;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public Venue setPrimary(VenueId venueId, String imageId) {
    var callerUserId = currentUser.getUserId();

    var venue =
        venues
            .findById(venueId)
            .orElseThrow(() -> new NotFoundException("Venue not found: " + venueId));

    ownership.assertOwner(callerUserId, venue);

    venue.setPrimaryById(imageId);
    venues.save(venue);
    return venue;
  }
}
