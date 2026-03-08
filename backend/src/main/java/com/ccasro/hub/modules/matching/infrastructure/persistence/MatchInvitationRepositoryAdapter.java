package com.ccasro.hub.modules.matching.infrastructure.persistence;

import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.MatchInvitationStatus;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchInvitationRepositoryAdapter implements MatchInvitationRepositoryPort {

  private final MatchInvitationJpaRepository jpa;

  @Override
  public void saveAll(List<MatchInvitation> invitations) {
    jpa.saveAll(invitations.stream().map(this::toEntity).toList());
  }

  @Override
  public void save(MatchInvitation invitation) {
    jpa.save(toEntity(invitation));
  }

  @Override
  public Optional<MatchInvitation> findById(UUID id) {
    return jpa.findById(id).map(this::toDomain);
  }

  @Override
  public List<MatchInvitation> findByPlayerId(UUID playerId) {
    return jpa.findByPlayerId(playerId).stream().map(this::toDomain).toList();
  }

  @Override
  public List<MatchInvitation> findByMatchRequestId(UUID matchRequestId) {
    return jpa.findByMatchRequestId(matchRequestId).stream().map(this::toDomain).toList();
  }

  @Override
  public void expireByMatchRequestId(UUID matchRequestId, Instant now) {
    jpa.expirePendingByMatchRequestId(matchRequestId, now);
  }

  private MatchInvitation toDomain(MatchInvitationEntity e) {
    return MatchInvitation.reconstitute(
        e.getId(),
        e.getMatchRequestId(),
        e.getPlayerId(),
        e.getPlayerEmail(),
        MatchInvitationStatus.valueOf(e.getStatus()),
        e.getSentAt(),
        e.getRespondedAt(),
        e.isFreeSubstitute());
  }

  private MatchInvitationEntity toEntity(MatchInvitation i) {
    MatchInvitationEntity e = new MatchInvitationEntity();
    e.setId(i.getId());
    e.setMatchRequestId(i.getMatchRequestId());
    e.setPlayerId(i.getPlayerId());
    e.setPlayerEmail(i.getPlayerEmail());
    e.setStatus(i.getStatus().name());
    e.setSentAt(i.getSentAt());
    e.setRespondedAt(i.getRespondedAt());
    e.setFreeSubstitute(i.isFreeSubstitute());
    return e;
  }
}
