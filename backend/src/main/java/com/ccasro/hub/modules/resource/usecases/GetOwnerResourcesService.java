package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.infrastructure.api.dto.ResourceResponse;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOwnerResourcesService {

  private final ResourceRepositoryPort resourceRepository;
  private final VenueRepositoryPort venueRepository;
  private final CurrentUserProvider currentUser;

  public List<ResourceResponse> execute() {
    List<Venue> myVenues = venueRepository.findByOwnerId(currentUser.getUserId());

    List<UUID> venueIds = myVenues.stream().map(v -> v.getId().value()).toList();

    Map<UUID, Venue> venueById =
        myVenues.stream().collect(Collectors.toMap(v -> v.getId().value(), v -> v));

    return resourceRepository.findByVenueIds(venueIds).stream()
        .map(ResourceResponse::from)
        .toList();
  }
}
