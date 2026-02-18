package com.ccasro.hub.modules.catalog.application.usecase.resource;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ActivateResourceUseCase {
  private final ResourceRepositoryPort resources;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  @Transactional
  public Resource activate(ResourceId id) {
    var res =
        resources.findById(id).orElseThrow(() -> new NotFoundException("Resource not found " + id));

    ownership.assertOwner(currentUser.getUserId(), res.venueId());
    res.activate();
    resources.save(res);
    return res;
  }
}
