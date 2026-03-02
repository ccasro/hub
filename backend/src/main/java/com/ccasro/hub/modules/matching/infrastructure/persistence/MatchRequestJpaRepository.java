package com.ccasro.hub.modules.matching.infrastructure.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRequestJpaRepository extends JpaRepository<MatchRequestEntity, UUID> {

  List<MatchRequestEntity> findByOrganizerId(UUID organizerId);

  Optional<MatchRequestEntity> findByInvitationToken(UUID invitationToken);

  @Query(
      """
        SELECT m FROM MatchRequestEntity m
        JOIN m.players p
        WHERE p.playerId = :playerId
        """)
  List<MatchRequestEntity> findByPlayerId(@Param("playerId") UUID playerId);

  @Query(
      """
        SELECT m FROM MatchRequestEntity m
        WHERE m.status = 'OPEN'
        AND m.expiresAt < :now
        """)
  List<MatchRequestEntity> findOpenAndExpired(@Param("now") java.time.Instant now);

  @Query(
      """
    SELECT m FROM MatchRequestEntity m
    WHERE m.resourceId = :resourceId
    AND m.bookingDate = :date
    AND m.startTime = :startTime
    AND m.status IN ('OPEN', 'FULL')
    """)
  Optional<MatchRequestEntity> findActiveByResourceAndSlot(
      @Param("resourceId") UUID resourceId,
      @Param("date") LocalDate date,
      @Param("startTime") LocalTime startTime);
}
