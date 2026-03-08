package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.exception.NotMatchOrganizerException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchNotificationPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelMatchRequestService {

  private final MatchRequestRepositoryPort matchRepository;
  private final MatchInvitationRepositoryPort invitationRepository;
  private final MatchPlayerPaymentService matchPlayerPaymentService;
  private final UserProfileRepositoryPort userRepository;
  private final MatchNotificationPort notificationPort;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public void execute(UUID matchId) {
    UserId currentUserId = currentUser.getUserId();

    // Load match first to verify organizer before attempting atomic cancel
    MatchRequest match =
        matchRepository
            .findById(new MatchRequestId(matchId))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    if (!match.getOrganizerId().equals(currentUserId)) {
      throw new NotMatchOrganizerException();
    }

    // Atomic UPDATE: sets status=CANCELLED only if currently in a cancellable state.
    // Returns false if a concurrent request already cancelled it (0 rows affected).
    boolean cancelled = matchRepository.cancelIfActive(new MatchRequestId(matchId));
    if (!cancelled) {
      throw new IllegalStateException("Match is not in a cancellable state");
    }

    Instant now = clock.instant();
    Instant cooldownThreshold = now.minus(Duration.ofHours(24));
    userRepository.tryRecordMatchCancellation(currentUserId, now, cooldownThreshold);

    invitationRepository.expireByMatchRequestId(matchId, clock.instant());

    refundJoinedPlayers(match);
    matchPlayerPaymentService.cancelMatchBooking(match);
    notifyJoinedPlayers(match, currentUserId);
  }

  private void refundJoinedPlayers(MatchRequest match) {
    match
        .getPlayers()
        .forEach(p -> matchPlayerPaymentService.refundPlayerPayment(match, p.getPlayerId()));
  }

  private void notifyJoinedPlayers(MatchRequest match, UserId organizerId) {
    Set<UserId> joinedPlayerIds =
        match.getPlayers().stream()
            .map(p -> p.getPlayerId())
            .filter(id -> !id.equals(organizerId))
            .collect(Collectors.toSet());

    if (joinedPlayerIds.isEmpty()) return;

    try {
      Map<UserId, String> emails = userRepository.findEmailsByIds(joinedPlayerIds);
      List<String> playerEmails = List.copyOf(emails.values());
      if (!playerEmails.isEmpty()) {
        notificationPort.notifyMatchCancelled(match, playerEmails);
      }
    } catch (Exception e) {
      log.warn("Failed to send match cancellation notifications: {}", e.getMessage());
    }
  }
}
