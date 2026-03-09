package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.ResourceInfoPort;
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
  private final ResourceInfoPort resourceInfoPort;

  public record MatchView(
      MatchRequest matchRequest, String resourceName, String venueName, String venueCity) {}

  @Transactional(readOnly = true)
  public MatchView findById(UUID id) {
    MatchRequest match =
        matchRepository
            .findById(MatchRequestId.of(id))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));
    return enrich(match);
  }

  @Transactional(readOnly = true)
  public MatchView findByToken(String token) {
    MatchRequest match =
        matchRepository
            .findByInvitationToken(InvitationToken.of(UUID.fromString(token)))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));
    return enrich(match);
  }

  private MatchView enrich(MatchRequest match) {
    return resourceInfoPort
        .findByResourceId(match.getResourceId().value())
        .map(info -> new MatchView(match, info.resourceName(), info.venueName(), info.venueCity()))
        .orElse(new MatchView(match, null, null, null));
  }
}
