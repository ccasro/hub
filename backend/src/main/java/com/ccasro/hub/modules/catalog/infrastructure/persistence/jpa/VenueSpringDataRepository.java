package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa;

import com.ccasro.hub.modules.catalog.domain.model.venue.VenueStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VenueSpringDataRepository extends JpaRepository<VenueJpaEntity, UUID> {

  @Query(
      """
        select v
        from VenueJpaEntity v
        where v.ownerUserId = :ownerUserId
        order by v.createdAt desc
    """)
  List<VenueJpaEntity> findMine(@Param("ownerUserId") UUID ownerUserId);

  @Query(
      """
        select v
        from VenueJpaEntity v
        where v.status = 'ACTIVE'
        order by v.createdAt desc
    """)
  List<VenueJpaEntity> findAllPublic();

  Optional<VenueJpaEntity> findByIdAndStatus(UUID id, VenueStatus status);

  boolean existsByIdAndOwnerUserId(UUID id, UUID ownerUserId);

  long countByOwnerUserId(UUID ownerUserId);
}
