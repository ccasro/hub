package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceScheduleJpaRepository extends JpaRepository<ResourceScheduleEntity, UUID> {

  List<ResourceScheduleEntity> findByResourceId(UUID resourceId);

  boolean existsByResourceIdAndDayOfWeek(UUID resourceId, DayOfWeek dayOfWeek);

  Optional<ResourceScheduleEntity> findByResourceIdAndDayOfWeek(
      UUID resourceId, DayOfWeek dayOfWeek);
}
