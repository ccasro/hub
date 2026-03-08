package com.ccasro.hub.modules.matching.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchInvitationJpaRepository extends JpaRepository<MatchInvitationEntity, UUID> {

  List<MatchInvitationEntity> findByPlayerId(UUID playerId);

  List<MatchInvitationEntity> findByMatchRequestId(UUID matchRequestId);

  @Modifying
  @Query(
      """
      UPDATE MatchInvitationEntity i
      SET i.status = 'EXPIRED', i.respondedAt = :now
      WHERE i.matchRequestId = :matchRequestId
        AND i.status = 'PENDING'
      """)
  void expirePendingByMatchRequestId(
      @Param("matchRequestId") UUID matchRequestId, @Param("now") Instant now);
}
