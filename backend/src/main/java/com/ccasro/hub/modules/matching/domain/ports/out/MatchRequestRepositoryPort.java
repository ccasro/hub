package com.ccasro.hub.modules.matching.domain.ports.out;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MatchRequestRepositoryPort {
  MatchRequest save(MatchRequest matchRequest);

  Optional<MatchRequest> findById(MatchRequestId id);

  Optional<MatchRequest> findByInvitationToken(InvitationToken token);

  Optional<MatchRequest> findActiveByResourceAndSlot(
      ResourceId resourceId, LocalDate date, LocalTime startTime);

  List<MatchRequest> findByOrganizerId(UserId organizerId);

  List<MatchRequest> findByPlayerId(UserId playerId);

  List<MatchRequest> findOpenAndExpired();
}
