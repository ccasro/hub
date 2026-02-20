package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.application.policy.VenueAccessPolicy;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import jakarta.transaction.Transactional;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddResourceImageService {

  private final ResourceRepositoryPort resourceRepository;
  private final CurrentUserProvider currentUser;
  private final VenueAccessPolicy venueAccessPolicy;
  private final Clock clock;

  @Transactional
  public Resource execute(ResourceId resourceId, ImageUrl imageUrl) {
    Resource resource =
        resourceRepository.findById(resourceId).orElseThrow(ResourceNotFoundException::new);

    venueAccessPolicy.assertOwner(resource.getVenueId(), currentUser.getUserId());

    resource.addImage(imageUrl, clock);
    return resourceRepository.save(resource);
  }
}
