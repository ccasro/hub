package com.ccasro.hub.modules.venue.usecases;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.exception.VenueNotFoundException;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("@authz.isAdmin()")
public class AdminVenueService {

  private final VenueRepositoryPort venueRepository;
  private final Clock clock;

  @Transactional(readOnly = true)
  public List<Venue> findAll(int page, int size) {
    return venueRepository.findAll(page, size);
  }

  @Transactional(readOnly = true)
  public List<Venue> findPending() {
    return venueRepository.findByStatus(VenueStatus.PENDING_REVIEW);
  }

  @Transactional
  @CacheEvict(
      value = {"venues", "venue-detail", "venues-with-count"},
      allEntries = true)
  public Venue approve(VenueId id) {
    Venue venue = venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
    venue.approve(clock);
    return venueRepository.save(venue);
  }

  @Transactional
  @CacheEvict(
      value = {"venues", "venue-detail", "venues-with-count"},
      allEntries = true)
  public Venue reject(VenueId id, String reason) {
    Venue venue = venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
    venue.reject(reason, clock);
    return venueRepository.save(venue);
  }

  @Transactional
  @CacheEvict(
      value = {"venues", "venue-detail", "venues-with-count"},
      allEntries = true)
  public Venue adminSuspend(VenueId id, String reason) {
    Venue venue = venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
    venue.adminSuspend(reason, clock);
    return venueRepository.save(venue);
  }
}
