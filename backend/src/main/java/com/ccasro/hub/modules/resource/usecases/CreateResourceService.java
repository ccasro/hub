package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.application.dto.CreateResourceCommand;
import com.ccasro.hub.modules.resource.application.policy.VenueAccessPolicy;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceName;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotDuration;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import jakarta.transaction.Transactional;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateResourceService {

  private final ResourceRepositoryPort resourceRepository;
  private final VenueAccessPolicy venueAccessPolicy;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public Resource execute(CreateResourceCommand cmd) {

    venueAccessPolicy.assertOwner(cmd.venueId(), currentUser.getUserId());

    Resource resource =
        Resource.create(
            cmd.venueId(),
            new ResourceName(cmd.name()),
            cmd.description(),
            cmd.type(),
            new SlotDuration(cmd.slotDurationMinutes()),
            clock);
    return resourceRepository.save(resource);
  }
}
