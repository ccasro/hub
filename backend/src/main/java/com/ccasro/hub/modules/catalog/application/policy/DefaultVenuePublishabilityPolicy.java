package com.ccasro.hub.modules.catalog.application.policy;

import com.ccasro.hub.common.domain.exception.DomainException;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultVenuePublishabilityPolicy implements VenuePublishabilityPolicy {

  private final ResourceRepositoryPort resources;

  @Override
  public void ensurePublishable(Venue venue) {
    int count = resources.countByVenueId(venue.id());
    if (count < 1) {
      throw new DomainException("At least 1 resource is required to activate venue");
    }
  }
}
