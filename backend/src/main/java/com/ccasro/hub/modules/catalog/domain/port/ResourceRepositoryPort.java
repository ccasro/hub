package com.ccasro.hub.modules.catalog.domain.port;

import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import java.util.Optional;

public interface ResourceRepositoryPort {
  void save(Resource resource);

  Optional<Resource> findById(ResourceId id);

  int countByVenueId(VenueId venueId);
}
