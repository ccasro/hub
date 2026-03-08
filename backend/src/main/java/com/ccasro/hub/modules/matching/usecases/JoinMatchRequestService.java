package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.PlayerTeam;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.exception.PlayerMatchBannedException;
import com.ccasro.hub.modules.matching.domain.exception.PlayerTimeConflictException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoinMatchRequestService {

  private final MatchRequestRepositoryPort matchRepository;
  private final MatchCompletionHandler matchCompletionHandler;
  private final MatchPlayerPaymentService matchPlayerPaymentService;
  private final UserProfileRepositoryPort userRepository;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public MatchRequest execute(InvitationToken token, PlayerTeam team) {

    UserId playerId = currentUser.getUserId();

    userRepository
        .findById(playerId)
        .ifPresent(
            profile -> {
              if (profile.isMatchBanned(clock)) throw new PlayerMatchBannedException();
            });

    MatchRequest matchRequest =
        matchRepository
            .findByInvitationToken(token)
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    checkNoTimeConflict(playerId, matchRequest);

    matchRequest.join(playerId, team, clock);

    matchPlayerPaymentService.createPaymentForPlayer(matchRequest, playerId);

    if (matchRequest.isFull()) {
      matchCompletionHandler.onMatchFull(matchRequest);
    }

    matchRepository.save(matchRequest);
    return matchRequest;
  }

  private void checkNoTimeConflict(UserId playerId, MatchRequest target) {
    LocalTime targetEnd = target.getStartTime().plusMinutes(target.getSlotDurationMinutes());
    boolean conflict =
        matchRepository.findActiveByPlayerAndDate(playerId, target.getBookingDate()).stream()
            .anyMatch(
                existing -> {
                  LocalTime existingEnd =
                      existing.getStartTime().plusMinutes(existing.getSlotDurationMinutes());
                  return target.getStartTime().isBefore(existingEnd)
                      && existing.getStartTime().isBefore(targetEnd);
                });
    if (conflict) throw new PlayerTimeConflictException();
  }
}
