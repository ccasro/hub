package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import com.ccasro.hub.modules.resource.infrastructure.persistence.projection.ResourceLiteProjection;
import com.ccasro.hub.modules.resource.infrastructure.persistence.projection.VenueIdCountProjection;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceJpaRepository extends JpaRepository<ResourceEntity, UUID> {
  List<ResourceEntity> findByVenueId(UUID venueId);

  List<ResourceEntity> findByVenueIdAndStatus(UUID venueId, ResourceStatus status);

  List<ResourceEntity> findByStatus(ResourceStatus status);

  long countByVenueIdAndStatus(UUID venueId, ResourceStatus status);

  @Query(
      """
  select r.venueId as venueId, count(r) as cnt
  from ResourceEntity r
  where r.status = :status
    and r.venueId in :venueIds
  group by r.venueId
""")
  List<VenueIdCountProjection> countByVenueIdsAndStatus(
      @Param("venueIds") List<UUID> venueIds, @Param("status") ResourceStatus status);

  @Query(
      """
  select r.id as id, r.name as name, r.venueId as venueId
  from ResourceEntity r
  where r.id in :ids
""")
  List<ResourceLiteProjection> findLiteByIds(@Param("ids") Collection<UUID> ids);

  long countByStatus(ResourceStatus status);

  List<ResourceEntity> findByVenueIdIn(Collection<UUID> venueIds);
}
