package com.ccasro.hub.modules.resource.domain.ports.out;

import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import java.util.List;
import java.util.Optional;

public interface ResourceRepositoryPort {
  Resource save(Resource resource);

  Optional<Resource> findById(ResourceId id);

  List<Resource> findByVenueId(VenueId venueId);

  List<Resource> findActiveByVenueId(VenueId venueId);

  List<Resource> findByStatus(ResourceStatus status);

  List<Resource> findAll(int page, int size);
}
