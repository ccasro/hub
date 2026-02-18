package com.ccasro.hub.modules.catalog.application.usecase.resource.image;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SetPrimaryResourceImageUseCase {

  private final ResourceRepositoryPort resources;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public Resource setPrimary(ResourceId resourceId, String imageId) {
    var callerUserId = currentUser.getUserId();

    var resource =
        resources
            .findById(resourceId)
            .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));

    ownership.assertOwner(callerUserId, resource.venueId());

    resource.setPrimaryById(imageId);
    resources.save(resource);
    return resource;
  }
}
