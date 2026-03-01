package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOwnerResourcesService {

  private final ResourceRepositoryPort resourceRepository;
  private final VenueRepositoryPort venueRepository;
  private final CurrentUserProvider currentUser;

  public List<Resource> execute() {
    List<Venue> myVenues = venueRepository.findByOwnerId(currentUser.getUserId());

    List<UUID> venueIds = myVenues.stream().map(v -> v.getId().value()).toList();

    return resourceRepository.findByVenueIds(venueIds);
  }
}
