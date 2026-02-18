package com.ccasro.hub.modules.catalog.application.usecase.resource;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SuspendResourceUseCase {
  private final ResourceRepositoryPort resources;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public void suspend(ResourceId resourceId) {

    var callerId = currentUser.getUserId();

    var resource =
        resources
            .findById(resourceId)
            .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));

    ownership.assertOwner(callerId, resource.venueId());

    resource.suspend();
    resources.save(resource);
  }
}
