package com.ccasro.hub.modules.matching.domain.ports.out;

import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchInvitationRepositoryPort {
  void saveAll(List<MatchInvitation> invitations);

  void save(MatchInvitation invitation);

  Optional<MatchInvitation> findById(UUID id);

  List<MatchInvitation> findByPlayerId(UUID playerId);

  List<MatchInvitation> findByMatchRequestId(UUID matchRequestId);

  void expireByMatchRequestId(UUID matchRequestId, java.time.Instant now);
}
