package com.ccasro.hub.modules.catalog.application.usecase.resource.image;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.common.domain.media.MediaKey;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import com.ccasro.hub.modules.media.application.MediaStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteResourceImageUseCase {

  private final ResourceRepositoryPort resources;
  private final CurrentUserProvider currentUser;
  private final MediaStoragePort mediaStorage;
  private final VenueOwnershipPolicy ownership;

  public Resource delete(ResourceId resourceId, String imageId) {
    var callerUserId = currentUser.getUserId();
    var principalId = currentUser.getSub();

    var resource =
        resources
            .findById(resourceId)
            .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));

    ownership.assertOwner(callerUserId, resource.venueId());

    var img =
        resource.images().stream()
            .filter(i -> i.id().toString().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Image not found: " + imageId));

    String expectedFolder = MediaKey.resourceImagesFolder(principalId, resourceId.toString());
    String expectedPrefix = MediaKey.ensureTrailingSlash(expectedFolder);
    if (!img.publicId().value().startsWith(expectedPrefix)) {
      throw new IllegalArgumentException("Image publicId does not belong to this resource");
    }

    mediaStorage.deleteByPublicId(img.publicId().value());

    resource.removeImageById(imageId);
    resources.save(resource);

    return resource;
  }
}
