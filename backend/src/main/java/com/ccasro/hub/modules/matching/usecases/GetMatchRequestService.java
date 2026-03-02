package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMatchRequestService {

  private final MatchRequestRepositoryPort matchRepository;

  @Transactional(readOnly = true)
  public MatchRequest findById(UUID id) {
    return matchRepository
        .findById(MatchRequestId.of(id))
        .orElseThrow(() -> new MatchNotFoundException("Match not found"));
  }

  @Transactional(readOnly = true)
  public MatchRequest findByToken(String token) {
    return matchRepository
        .findByInvitationToken(InvitationToken.of(UUID.fromString(token)))
        .orElseThrow(() -> new MatchNotFoundException("Match not found"));
  }
}
