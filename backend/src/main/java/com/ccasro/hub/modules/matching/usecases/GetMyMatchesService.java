package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.ResourceInfoPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyMatchesService {

  private final MatchRequestRepositoryPort matchRepository;
  private final ResourceInfoPort resourceInfoPort;
  private final CurrentUserProvider currentUser;

  public record MatchView(
      MatchRequest matchRequest, String resourceName, String venueName, String venueCity) {}

  @Transactional(readOnly = true)
  public List<MatchView> execute() {
    List<MatchRequest> matches = matchRepository.findByPlayerId(currentUser.getUserId());

    if (matches.isEmpty()) return List.of();

    Set<UUID> resourceIds =
        matches.stream().map(m -> m.getResourceId().value()).collect(Collectors.toSet());

    Map<UUID, ResourceInfoPort.ResourceInfo> infoMap =
        resourceInfoPort.findByResourceIds(resourceIds);

    return matches.stream()
        .map(
            m -> {
              ResourceInfoPort.ResourceInfo info = infoMap.get(m.getResourceId().value());
              return info != null
                  ? new MatchView(m, info.resourceName(), info.venueName(), info.venueCity())
                  : new MatchView(m, null, null, null);
            })
        .toList();
  }
}
