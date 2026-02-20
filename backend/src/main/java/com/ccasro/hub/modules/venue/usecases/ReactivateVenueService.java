package com.ccasro.hub.modules.venue.usecases;

import com.ccasro.hub.modules.venue.application.policy.VenuePolicy;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.exception.VenueNotFoundException;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactivateVenueService {

  private final VenueRepositoryPort venueRepository;
  private final CurrentUserProvider currentUser;
  private final VenuePolicy venuePolicy;
  private final Clock clock;

  public void execute(VenueId venueId) {
    Venue venue = venueRepository.findById(venueId).orElseThrow(VenueNotFoundException::new);

    venuePolicy.assertOwner(venue, currentUser.getUserId());

    venue.reactivate(clock);
    venueRepository.save(venue);
  }
}
