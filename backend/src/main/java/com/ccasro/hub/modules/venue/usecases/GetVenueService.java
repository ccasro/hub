package com.ccasro.hub.modules.venue.usecases;

import com.ccasro.hub.modules.resource.domain.ports.out.ResourceCountPort;
import com.ccasro.hub.modules.venue.application.dto.VenueWithCount;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.exception.VenueNotFoundException;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetVenueService {

  private final VenueRepositoryPort venueRepository;
  private final ResourceCountPort resourceCountPort;

  public Venue findById(VenueId id) {
    return venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
  }

  public Venue findPublicById(VenueId id) {
    Venue venue = findById(id);
    if (!venue.isPubliclyVisible()) throw new VenueNotFoundException();
    return venue;
  }

  public List<Venue> findAllActive() {
    return venueRepository.findAllActive();
  }

  public List<VenueWithCount> findAllActiveWithResourceCount() {
    var venues = venueRepository.findAllActive();
    var ids = venues.stream().map(v -> v.getId().value()).toList();

    var counts = resourceCountPort.countActiveByVenueIds(ids);

    return venues.stream()
        .map(v -> new VenueWithCount(v, counts.getOrDefault(v.getId().value(), 0)))
        .toList();
  }

  public List<Venue> findMyVenues(UserId ownerId) {
    return venueRepository.findByOwnerId(ownerId);
  }

  public List<Venue> findNearby(double lat, double lng, double radiusMeters) {
    return venueRepository.findActiveNearby(lat, lng, radiusMeters);
  }
}
