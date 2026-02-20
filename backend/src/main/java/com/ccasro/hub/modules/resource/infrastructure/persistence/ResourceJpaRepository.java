package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceJpaRepository extends JpaRepository<ResourceEntity, UUID> {
  List<ResourceEntity> findByVenueId(UUID venueId);

  List<ResourceEntity> findByVenueIdAndStatus(UUID venueId, ResourceStatus status);

  List<ResourceEntity> findByStatus(ResourceStatus status);
}
