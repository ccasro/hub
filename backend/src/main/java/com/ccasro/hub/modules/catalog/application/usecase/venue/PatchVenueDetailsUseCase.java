package com.ccasro.hub.modules.catalog.application.usecase.venue;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.modules.catalog.application.command.PatchVenueDetailsCommand;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.venue.*;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PatchVenueDetailsUseCase {

  private final VenueRepositoryPort venues;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public Venue patch(VenueId venueId, PatchVenueDetailsCommand cmd) {
    var callerId = currentUser.getUserId();

    var venue =
        venues
            .findById(venueId)
            .orElseThrow(() -> new NotFoundException("Venue not found: " + venueId));

    ownership.assertOwner(callerId, venue);

    if (cmd.name() != null) {
      venue.rename(new VenueName(cmd.name()));
    }
    if (cmd.description() != null) {
      venue.updateDescription(new Description(cmd.description()));
    }
    if (cmd.address() != null) {
      venue.updateAddress(
          new Address(
              cmd.address().street(),
              cmd.address().city(),
              cmd.address().postalCode(),
              cmd.address().country()));
    }
    if (cmd.location() != null) {
      venue.updateLocation(new GeoLocation(cmd.location().latitude(), cmd.location().longitude()));
    }

    venues.save(venue);
    return venue;
  }
}
