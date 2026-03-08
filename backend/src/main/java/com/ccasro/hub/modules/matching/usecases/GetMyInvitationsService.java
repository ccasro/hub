package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.resource.infrastructure.persistence.ResourceJpaRepository;
import com.ccasro.hub.modules.resource.infrastructure.persistence.projection.ResourceLiteProjection;
import com.ccasro.hub.modules.venue.infrastructure.persistence.VenueJpaRepository;
import com.ccasro.hub.modules.venue.infrastructure.persistence.projection.VenueLiteProjection;
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
public class GetMyInvitationsService {

  private final MatchInvitationRepositoryPort invitationRepository;
  private final MatchRequestRepositoryPort matchRepository;
  private final ResourceJpaRepository resourceRepository;
  private final VenueJpaRepository venueRepository;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<MatchInvitationView> execute() {
    UUID playerId = currentUser.getUserId().value();

    List<MatchInvitation> invitations = invitationRepository.findByPlayerId(playerId);

    if (invitations.isEmpty()) return List.of();

    Set<UUID> matchIds =
        invitations.stream().map(MatchInvitation::getMatchRequestId).collect(Collectors.toSet());

    Map<UUID, MatchRequest> matchRequests =
        matchRepository.findAllById(matchIds).stream()
            .collect(Collectors.toMap(m -> m.getId().value(), m -> m));

    Set<UUID> resourceIds =
        matchRequests.values().stream()
            .map(m -> m.getResourceId().value())
            .collect(Collectors.toSet());

    Map<UUID, ResourceLiteProjection> resources =
        resourceRepository.findLiteByIds(resourceIds).stream()
            .collect(Collectors.toMap(ResourceLiteProjection::getId, r -> r));

    Set<UUID> venueIds =
        resources.values().stream()
            .map(ResourceLiteProjection::getVenueId)
            .collect(Collectors.toSet());

    Map<UUID, VenueLiteProjection> venues =
        venueRepository.findLiteByIds(venueIds).stream()
            .collect(Collectors.toMap(VenueLiteProjection::getId, v -> v));

    return invitations.stream()
        .map(
            inv -> {
              MatchRequest match = matchRequests.get(inv.getMatchRequestId());
              ResourceLiteProjection resource =
                  match != null ? resources.get(match.getResourceId().value()) : null;
              VenueLiteProjection venue =
                  resource != null ? venues.get(resource.getVenueId()) : null;
              return new MatchInvitationView(
                  inv,
                  match,
                  resource != null ? resource.getName() : null,
                  venue != null ? venue.getName() : null,
                  venue != null ? venue.getCity() : null);
            })
        .toList();
  }

  public record MatchInvitationView(
      MatchInvitation invitation,
      MatchRequest matchRequest,
      String resourceName,
      String venueName,
      String venueCity) {}
}
