package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.application.dto.SetScheduleCommand;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.venue.application.ports.in.VenueAccessPolicy;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetScheduleService {

  private final ResourceRepositoryPort resourceRepository;
  private final VenueAccessPolicy venueAccessPolicy;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  @PreAuthorize("@authz.isOwner()")
  public Resource execute(SetScheduleCommand cmd) {
    Resource resource =
        resourceRepository.findById(cmd.resourceId()).orElseThrow(ResourceNotFoundException::new);

    venueAccessPolicy.assertOwner(resource.getVenueId().value(), currentUser.getUserId());

    if (cmd.openingTime() == null || cmd.closingTime() == null) {
      return resourceRepository.removeSchedule(cmd.resourceId(), cmd.dayOfWeek());
    } else {
      return resourceRepository.upsertSchedule(
          cmd.resourceId(), cmd.dayOfWeek(), cmd.openingTime(), cmd.closingTime());
    }
  }
}
