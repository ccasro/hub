package com.ccasro.hub.modules.matching.domain.ports.out;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MatchRequestRepositoryPort {
  MatchRequest save(MatchRequest matchRequest);

  Optional<MatchRequest> findById(MatchRequestId id);

  Optional<MatchRequest> findByInvitationToken(InvitationToken token);

  Optional<MatchRequest> findActiveByResourceAndSlot(
      ResourceId resourceId, LocalDate date, LocalTime startTime);

  List<MatchRequest> findByOrganizerId(UserId organizerId);

  List<MatchRequest> findByPlayerId(UserId playerId);

  List<MatchRequest> findActiveByPlayerAndDate(UserId playerId, LocalDate date);

  List<MatchRequest> findOpenAndExpired();

  List<MatchRequest> findAwaitingPaymentExpired(Instant deadline);

  List<MatchRequest> findFullEndedBetween(Instant from, Instant to);

  List<MatchRequest> findAllById(Set<UUID> ids);

  /**
   * Atomically transitions the match to CANCELLED only if it is in a cancellable state. Returns
   * true if the match was cancelled, false if it was already in a terminal state.
   */
  boolean cancelIfActive(MatchRequestId id);

  long countActiveByOrganizer(UserId organizerId);
}
