package com.ccasro.hub.modules.venue.usecases;

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

  public Venue findById(VenueId id) {
    return venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
  }

  public List<Venue> findAllActive() {
    return venueRepository.findAllActive();
  }

  public List<Venue> findMyVenues(UserId ownerId) {
    return venueRepository.findByOwnerId(ownerId);
  }

  public List<Venue> findNearby(double lat, double lng, double radiusMeters) {
    return venueRepository.findActiveNearby(lat, lng, radiusMeters);
  }
}
