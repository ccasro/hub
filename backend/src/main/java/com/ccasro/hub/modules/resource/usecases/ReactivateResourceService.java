package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.application.policy.VenueAccessPolicy;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactivateResourceService {

  private final ResourceRepositoryPort resourceRepository;
  private final VenueAccessPolicy venueAccessPolicy;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public void execute(ResourceId resourceId) {
    Resource resource =
        resourceRepository.findById(resourceId).orElseThrow(ResourceNotFoundException::new);
    venueAccessPolicy.assertOwner(resource.getVenueId(), currentUser.getUserId());
    resource.reactivate(clock);
    resourceRepository.save(resource);
  }
}
