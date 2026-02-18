package com.ccasro.hub.modules.catalog.application.usecase.venue;

import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetVenueByIdUseCase {

  private final VenueRepositoryPort venues;

  @Transactional(readOnly = true)
  public Venue get(VenueId id) {
    return venues.findById(id).orElseThrow(() -> new NotFoundException("Venue not found: " + id));
  }
}
