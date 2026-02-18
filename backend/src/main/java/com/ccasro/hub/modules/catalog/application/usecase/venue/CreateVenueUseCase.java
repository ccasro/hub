package com.ccasro.hub.modules.catalog.application.usecase.venue;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.modules.catalog.application.command.CreateVenueCommand;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueName;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateVenueUseCase {

  private final VenueRepositoryPort venues;
  private final CurrentUserProvider currentUser;

  @Transactional
  public Venue create(CreateVenueCommand cmd) {
    Objects.requireNonNull(cmd, "cmd is required");

    var ownerId = currentUser.getUserId();

    Venue venue =
        Venue.create(ownerId, new VenueName(cmd.name()), new Description(cmd.description()));

    venues.save(venue);
    return venue;
  }
}
