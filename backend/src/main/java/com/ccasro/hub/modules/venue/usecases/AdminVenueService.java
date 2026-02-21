package com.ccasro.hub.modules.venue.usecases;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.exception.VenueNotFoundException;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import com.ccasro.hub.shared.application.ports.CurrentUserContextProvider;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("@authz.isAdmin()")
public class AdminVenueService {

  private final VenueRepositoryPort venueRepository;
  private final CurrentUserContextProvider current;
  private final Clock clock;

  private void requireAdmin() {
    if (!current.role().isAdmin()) {
      throw new AccessDeniedException("Admin access required");
    }
  }

  @Transactional(readOnly = true)
  public List<Venue> findAll(int page, int size) {
    requireAdmin();
    return venueRepository.findAll(page, size);
  }

  @Transactional(readOnly = true)
  public List<Venue> findPending() {
    requireAdmin();
    return venueRepository.findByStatus(VenueStatus.PENDING_REVIEW);
  }

  @Transactional
  public Venue approve(VenueId id) {
    requireAdmin();
    Venue venue = venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
    venue.approve(clock);
    return venueRepository.save(venue);
  }

  @Transactional
  public Venue reject(VenueId id, String reason) {
    requireAdmin();
    Venue venue = venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
    venue.reject(reason, clock);
    return venueRepository.save(venue);
  }

  @Transactional
  public Venue adminSuspend(VenueId id, String reason) {
    requireAdmin();
    Venue venue = venueRepository.findById(id).orElseThrow(VenueNotFoundException::new);
    venue.adminSuspend(reason, clock);
    return venueRepository.save(venue);
  }
}
