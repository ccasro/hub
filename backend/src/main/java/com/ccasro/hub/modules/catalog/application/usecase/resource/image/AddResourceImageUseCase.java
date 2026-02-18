package com.ccasro.hub.modules.catalog.application.usecase.resource.image;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.common.domain.media.MediaKey;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddResourceImageUseCase {

  private final ResourceRepositoryPort resources;
  private final VenueRepositoryPort venues;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public Resource add(ResourceId resourceId, String publicId, String url) {
    Objects.requireNonNull(resourceId, "resourceId is required");
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(url, "url is required");

    var callerUserId = currentUser.getUserId();
    var principalId = currentUser.getSub();

    var resource =
        resources
            .findById(resourceId)
            .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));

    ownership.assertOwner(callerUserId, resource.venueId());

    String expectedFolder = MediaKey.resourceImagesFolder(principalId, resourceId.toString());
    String expectedPrefix = MediaKey.ensureTrailingSlash(expectedFolder);
    if (!publicId.startsWith(expectedPrefix)) {
      throw new IllegalArgumentException("Image publicId does not belong to this resource");
    }

    resource.addImage(publicId, url);
    resources.save(resource);
    return resource;
  }
}
