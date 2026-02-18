package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceImageSpringDataRepository
    extends JpaRepository<ResourceImageJpaEntity, UUID> {
  List<ResourceImageJpaEntity> findByResourceIdOrderByPositionAsc(UUID resourceId);

  void deleteByResourceId(UUID resourceId);
}
