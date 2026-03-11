package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.infrastructure.config.MatchingProperties;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.exception.MatchLeaveNotAllowedException;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.exception.NotMatchOrganizerException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveMatchRequestService {

  private final MatchRequestRepositoryPort matchRepository;
  private final MatchPlayerPaymentService matchPlayerPaymentService;
  private final CurrentUserProvider currentUser;
  private final MatchingProperties matchingProperties;
  private final Clock clock;

  @Transactional
  public void execute(UUID matchId) {
    UserId playerId = currentUser.getUserId();

    MatchRequest match =
        matchRepository
            .findById(new MatchRequestId(matchId))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    if (match.getOrganizerId().equals(playerId) && !match.isFull()) {
      throw new NotMatchOrganizerException(
          "Organizers cannot leave an open match, please cancel it instead");
    }

    boolean isParticipant =
        match.getPlayers().stream().anyMatch(p -> p.getPlayerId().equals(playerId));
    if (!isParticipant) {
      throw new IllegalStateException("You are not a participant of this match");
    }

    LocalDateTime matchStart = LocalDateTime.of(match.getBookingDate(), match.getStartTime());
    LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    if (now.isAfter(matchStart.minus(matchingProperties.getLeaveMatchMinHoursBefore()))) {
      throw new MatchLeaveNotAllowedException();
    }

    boolean wasFullBeforeLeave = match.isFull();

    matchPlayerPaymentService.refundPlayerPayment(match, playerId);

    match.removePlayer(playerId, clock);
    matchRepository.save(match);

    if (wasFullBeforeLeave && match.isOpen()) {
      matchPlayerPaymentService.revertBookingToPendingMatch(match);
    }
  }
}
