package com.ccasro.hub.modules.catalog.application.usecase.venue.image;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.common.domain.media.MediaKey;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddVenueImageUseCase {

  private final VenueRepositoryPort venues;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public Venue add(VenueId venueId, String publicId, String url) {
    Objects.requireNonNull(venueId, "venueId is required");
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(url, "url is required");

    var callerUserId = currentUser.getUserId();
    var principalId = currentUser.getSub();

    var venue =
        venues
            .findById(venueId)
            .orElseThrow(() -> new NotFoundException("Venue not found: " + venueId));

    ownership.assertOwner(callerUserId, venue);

    String expectedFolder = MediaKey.venueImagesFolder(principalId, venueId.toString());
    String expectedPrefix = MediaKey.ensureTrailingSlash(expectedFolder);
    if (!publicId.startsWith(expectedPrefix)) {
      throw new IllegalArgumentException("Image publicId does not belong to this venue");
    }

    venue.addImage(publicId, url);
    venues.save(venue);
    return venue;
  }
}
