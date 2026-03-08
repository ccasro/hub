package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.PlayerTeam;
import com.ccasro.hub.modules.matching.domain.exception.InvitationAlreadyRespondedException;
import com.ccasro.hub.modules.matching.domain.exception.InvitationNotFoundException;
import com.ccasro.hub.modules.matching.domain.exception.InvitationNotYoursException;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.exception.PlayerMatchBannedException;
import com.ccasro.hub.modules.matching.domain.exception.PlayerTimeConflictException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.LocalTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RespondToInvitationService {

  private final MatchInvitationRepositoryPort invitationRepository;
  private final MatchRequestRepositoryPort matchRepository;
  private final MatchCompletionHandler matchCompletionHandler;
  private final MatchPlayerPaymentService matchPlayerPaymentService;
  private final UserProfileRepositoryPort userRepository;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public MatchRequest accept(UUID invitationId, PlayerTeam team) {
    MatchInvitation invitation = loadAndVerify(invitationId);

    UserId playerId = new UserId(invitation.getPlayerId());

    if (!invitation.isFreeSubstitute()) {
      userRepository
          .findById(playerId)
          .ifPresent(
              profile -> {
                if (profile.isMatchBanned(clock)) throw new PlayerMatchBannedException();
              });
    }

    MatchRequest matchRequest =
        matchRepository
            .findById(new MatchRequestId(invitation.getMatchRequestId()))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    checkNoTimeConflict(playerId, matchRequest);

    matchRequest.join(playerId, team, clock);

    if (!invitation.isFreeSubstitute()) {
      matchPlayerPaymentService.createPaymentForPlayer(matchRequest, playerId);
    }

    if (matchRequest.isFull()) {
      matchCompletionHandler.onMatchFull(matchRequest);
    }

    matchRepository.save(matchRequest);

    invitation.accept(clock.instant());
    invitationRepository.save(invitation);

    return matchRequest;
  }

  @Transactional
  public void decline(UUID invitationId) {
    MatchInvitation invitation = loadAndVerify(invitationId);
    invitation.decline(clock.instant());
    invitationRepository.save(invitation);
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

  private MatchInvitation loadAndVerify(UUID invitationId) {
    UserId currentUserId = currentUser.getUserId();

    MatchInvitation invitation =
        invitationRepository.findById(invitationId).orElseThrow(InvitationNotFoundException::new);

    if (!invitation.getPlayerId().equals(currentUserId.value())) {
      throw new InvitationNotYoursException();
    }

    if (!invitation.isPending()) {
      throw new InvitationAlreadyRespondedException();
    }

    return invitation;
  }
}
