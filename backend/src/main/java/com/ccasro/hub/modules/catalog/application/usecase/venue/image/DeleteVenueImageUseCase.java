package com.ccasro.hub.modules.catalog.application.usecase.venue.image;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.common.domain.media.MediaKey;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import com.ccasro.hub.modules.media.application.MediaStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteVenueImageUseCase {

  private final VenueRepositoryPort venues;
  private final CurrentUserProvider currentUser;
  private final MediaStoragePort mediaStorage;
  private final VenueOwnershipPolicy ownership;

  public Venue delete(VenueId venueId, String imageId) {
    var callerUserId = currentUser.getUserId();
    var principalId = currentUser.getSub();

    var venue =
        venues
            .findById(venueId)
            .orElseThrow(() -> new NotFoundException("Venue not found: " + venueId));

    ownership.assertOwner(callerUserId, venue);

    var img =
        venue.images().stream()
            .filter(i -> i.id().toString().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Image not found: " + imageId));

    String expectedFolder = MediaKey.venueImagesFolder(principalId, venueId.toString());
    String expectedPrefix = MediaKey.ensureTrailingSlash(expectedFolder);
    if (!img.publicId().value().startsWith(expectedPrefix)) {
      throw new IllegalArgumentException("Image publicId does not belong to this venue");
    }

    mediaStorage.deleteByPublicId(img.publicId().value());

    venue.removeImageById(imageId);
    venues.save(venue);
    return venue;
  }
}
