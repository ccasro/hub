package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceSpringDataRepository extends JpaRepository<ResourceJpaEntity, UUID> {

  @Query(
      """
        select r
        from ResourceJpaEntity r, VenueJpaEntity v
        where v.id = r.venueId
          and v.ownerUserId = :ownerUserId
          and r.venueId = :venueId
        order by r.createdAt desc
    """)
  List<ResourceJpaEntity> findByOwnerUserIdAndVenueId(
      @Param("ownerUserId") UUID ownerUserId, @Param("venueId") UUID venueId);

  @Query(
      """
        select r
        from ResourceJpaEntity r, VenueJpaEntity v
        where v.id = r.venueId
          and v.ownerUserId = :ownerUserId
        order by r.createdAt desc
    """)
  List<ResourceJpaEntity> findByOwnerUserId(@Param("ownerUserId") UUID ownerUserId);

  @Query(
      """
    select r
    from ResourceJpaEntity r, VenueJpaEntity v
    where r.venueId = v.id
      and r.status = 'ACTIVE'
      and v.status = 'ACTIVE'
    order by r.createdAt desc
""")
  List<ResourceJpaEntity> findAllPublic();

  @Query(
      """
    select r
    from ResourceJpaEntity r, VenueJpaEntity v
    where r.venueId = v.id
      and r.venueId = :venueId
      and r.status = 'ACTIVE'
      and v.status = 'ACTIVE'
    order by r.createdAt desc
""")
  List<ResourceJpaEntity> findPublicByVenueId(@Param("venueId") UUID venueId);

  long countByVenueId(UUID venueId);
}
