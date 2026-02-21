package com.ccasro.hub.modules.venue.application.policy;

import com.ccasro.hub.modules.venue.application.ports.in.VenueAccessPolicy;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VenuePolicy implements VenueAccessPolicy {

  private final VenueRepositoryPort venueRepository;

  @Override
  @Transactional(readOnly = true)
  public void assertOwner(UUID venueId, UserId userId) {
    if (!venueRepository.existsOwnedBy(VenueId.of(venueId), userId)) {
      throw new AccessDeniedException("You are not the owner");
    }
  }

  @Override
  public void assertOwner(Venue venue, UserId userId) {
    if (!venue.isOwnedBy(userId)) {
      throw new AccessDeniedException("You are not the owner of this venue");
    }
  }
}
