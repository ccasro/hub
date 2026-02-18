package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueImageSpringDataRepository extends JpaRepository<VenueImageJpaEntity, UUID> {
  List<VenueImageJpaEntity> findByVenueIdOrderByPositionAsc(UUID venueId);

  void deleteByVenueId(UUID venueId);
}
