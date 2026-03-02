package com.ccasro.hub.modules.venue.infrastructure.persistence;

import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import com.ccasro.hub.modules.venue.infrastructure.persistence.projection.VenueLiteProjection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VenueJpaRepository extends JpaRepository<VenueEntity, UUID> {

  List<VenueEntity> findByStatus(VenueStatus status);

  List<VenueEntity> findByOwnerIdAndStatusNot(UUID ownerId, VenueStatus status);

  @Query(
      value =
          """
        SELECT * FROM venue
        WHERE status = 'ACTIVE'
        AND ST_DWithin(
            location,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radiusMeters
        )
        ORDER BY ST_Distance(
            location,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """,
      nativeQuery = true)
  List<VenueEntity> findActiveNearby(
      @Param("lat") double lat,
      @Param("lng") double lng,
      @Param("radiusMeters") double radiusMeters);

  @Query("select v from VenueEntity v where v.status = 'ACTIVE'")
  List<VenueEntity> findAllActive();

  boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

  @Query(
      value =
          """
    SELECT
        v.id                        AS id,
        v.name                      AS name,
        v.city                      AS city,
        ST_Y(v.location::geometry)  AS latitude,
        ST_X(v.location::geometry)  AS longitude
    FROM venue v
    WHERE v.id IN :ids
    """,
      nativeQuery = true)
  List<VenueLiteProjection> findLiteByIds(@Param("ids") Set<UUID> ids);

  long countByStatus(VenueStatus status);
}
