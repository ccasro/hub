package com.ccasro.hub.modules.venue.usecases;

import com.ccasro.hub.modules.venue.application.dto.UpdateVenueCommand;
import com.ccasro.hub.modules.venue.application.policy.VenuePolicy;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.exception.VenueNotFoundException;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.Address;
import com.ccasro.hub.modules.venue.domain.valueobjects.Coordinates;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueName;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVenueService {

  private final VenueRepositoryPort venueRepository;
  private final CurrentUserProvider currentUser;
  private final VenuePolicy venuePolicy;
  private final Clock clock;

  @Transactional
  @PreAuthorize("@authz.isOwner()")
  public Venue execute(UpdateVenueCommand cmd) {
    Venue venue = venueRepository.findById(cmd.venueId()).orElseThrow(VenueNotFoundException::new);

    venuePolicy.assertOwner(venue, currentUser.getUserId());

    Coordinates coordinates = new Coordinates(cmd.latitude(), cmd.longitude());

    venue.update(
        new VenueName(cmd.name()),
        cmd.description(),
        new Address(cmd.street(), cmd.city(), cmd.country(), cmd.postalCode()),
        coordinates,
        clock);
    return venueRepository.save(venue);
  }
}
