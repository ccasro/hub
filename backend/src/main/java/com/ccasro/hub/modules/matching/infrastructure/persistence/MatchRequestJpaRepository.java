package com.ccasro.hub.modules.matching.infrastructure.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
          AND p.leftAt IS NULL
        """)
  List<MatchRequestEntity> findByPlayerId(@Param("playerId") UUID playerId);

  @Query(
      """
        SELECT m FROM MatchRequestEntity m
        JOIN m.players p
        WHERE p.playerId = :playerId
          AND p.leftAt IS NULL
          AND m.bookingDate = :date
          AND m.status IN ('AWAITING_ORGANIZER_PAYMENT', 'OPEN', 'FULL')
        """)
  List<MatchRequestEntity> findActiveByPlayerAndDate(
      @Param("playerId") UUID playerId, @Param("date") java.time.LocalDate date);

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
        WHERE m.status = 'AWAITING_ORGANIZER_PAYMENT'
        AND m.createdAt < :deadline
        """)
  List<MatchRequestEntity> findAwaitingPaymentExpired(
      @Param("deadline") java.time.Instant deadline);

  @Query(
      value =
          """
      SELECT * FROM match_request
      WHERE status = 'FULL'
      AND (booking_date + start_time + (slot_duration_minutes || ' minutes')::interval)
          BETWEEN :from AND :to
      """,
      nativeQuery = true)
  List<MatchRequestEntity> findFullEndedBetween(
      @Param("from") java.time.Instant from, @Param("to") java.time.Instant to);

  @Modifying
  @Query(
      """
      UPDATE MatchRequestEntity m
      SET m.status = 'CANCELLED'
      WHERE m.id = :id
        AND m.status IN ('OPEN', 'AWAITING_ORGANIZER_PAYMENT')
      """)
  int cancelIfActive(@Param("id") UUID id);

  @Query(
      """
      SELECT COUNT(m) FROM MatchRequestEntity m
      WHERE m.organizerId = :organizerId
        AND m.status IN ('AWAITING_ORGANIZER_PAYMENT', 'OPEN', 'FULL')
      """)
  long countActiveByOrganizer(@Param("organizerId") UUID organizerId);

  @Query(
      """
    SELECT m FROM MatchRequestEntity m
    WHERE m.resourceId = :resourceId
    AND m.bookingDate = :date
    AND m.startTime = :startTime
    AND m.status IN ('AWAITING_ORGANIZER_PAYMENT', 'OPEN', 'FULL')
    """)
  Optional<MatchRequestEntity> findActiveByResourceAndSlot(
      @Param("resourceId") UUID resourceId,
      @Param("date") LocalDate date,
      @Param("startTime") LocalTime startTime);

  @Query(
      """
    SELECT m FROM MatchRequestEntity m
    WHERE m.resourceId IN :resourceIds
    AND m.status IN ('AWAITING_ORGANIZER_PAYMENT', 'OPEN', 'FULL')
    """)
  List<MatchRequestEntity> findActiveByResourceIds(@Param("resourceIds") Set<UUID> resourceIds);

  @Query(
      """
    SELECT m.id FROM MatchRequestEntity m
    JOIN m.players p
    WHERE p.playerId = :playerId
      AND p.leftAt IS NOT NULL
    """)
  Set<UUID> findMatchIdsWherePlayerLeft(@Param("playerId") UUID playerId);
}
