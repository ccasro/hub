package com.ccasro.hub.modules.venue.infrastructure.persistence;

import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VenueJpaRepository extends JpaRepository<VenueEntity, UUID> {

  List<VenueEntity> findByStatus(VenueStatus status);

  List<VenueEntity> findByOwnerIdAndStatusNot(UUID ownerId, VenueStatus status);

  Page<VenueEntity> findAll(Pageable pageable);

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
}
