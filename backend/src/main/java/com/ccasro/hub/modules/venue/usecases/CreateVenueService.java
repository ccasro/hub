package com.ccasro.hub.modules.venue.usecases;

import com.ccasro.hub.modules.venue.application.dto.CreateVenueCommand;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.Address;
import com.ccasro.hub.modules.venue.domain.valueobjects.Coordinates;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueName;
import com.ccasro.hub.shared.application.ports.CurrentUserContextProvider;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateVenueService {

  private final VenueRepositoryPort venueRepository;
  private final CurrentUserContextProvider currentUserRole;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public Venue execute(CreateVenueCommand cmd) {

    if (!currentUserRole.role().canManageVenues()) {
      throw new AccessDeniedException("You do not have authority to create venues");
    }

    Coordinates coordinates = new Coordinates(cmd.latitude(), cmd.longitude());

    Venue venue =
        Venue.create(
            currentUser.getUserId(),
            new VenueName(cmd.name()),
            cmd.description(),
            new Address(cmd.street(), cmd.city(), cmd.country(), cmd.postalCode()),
            coordinates,
            clock);
    return venueRepository.save(venue);
  }
}
