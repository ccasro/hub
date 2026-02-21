package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.application.dto.AddPriceRuleCommand;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.venue.application.ports.in.VenueAccessPolicy;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddPriceRuleService {

  private final ResourceRepositoryPort resourceRepository;
  private final VenueAccessPolicy venueAccessPolicy;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  @PreAuthorize("@authz.isOwner()")
  public Resource execute(AddPriceRuleCommand cmd) {
    Resource resource =
        resourceRepository.findById(cmd.resourceId()).orElseThrow(ResourceNotFoundException::new);

    venueAccessPolicy.assertOwner(resource.getVenueId().value(), currentUser.getUserId());

    resource.addPriceRule(
        cmd.dayType(), cmd.startTime(), cmd.endTime(), cmd.price(), cmd.currency(), clock);
    return resourceRepository.save(resource);
  }
}
