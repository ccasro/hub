package com.ccasro.hub.modules.venue.application.policy;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenuePolicy {

  public void assertOwner(Venue venue, UserId userId) {
    if (!venue.isOwnedBy(userId)) {
      throw new AccessDeniedException("You are not the owner of this venue");
    }
  }
}
