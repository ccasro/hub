package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.ports.out.EligiblePlayerPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchNotificationPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
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
public class ReportAbsenceService {

  private final MatchRequestRepositoryPort matchRepository;
  private final MatchInvitationRepositoryPort invitationRepository;
  private final MatchPlayerPaymentService matchPlayerPaymentService;
  private final EligiblePlayerPort eligiblePlayerPort;
  private final UserProfileRepositoryPort userRepository;
  private final MatchNotificationPort notificationPort;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public void execute(UUID matchId) {
    UserId playerId = currentUser.getUserId();

    MatchRequest match =
        matchRepository
            .findById(new MatchRequestId(matchId))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    boolean wasFullBeforeAbsence = match.isFull();

    match.reportAbsence(playerId);
    matchRepository.save(match);

    if (wasFullBeforeAbsence && match.isOpen()) {
      matchPlayerPaymentService.revertBookingToPendingMatch(match);
    }

    sendSubstituteInvitations(match, playerId);
    notifyRemainingPlayers(match, playerId);
  }

  private void sendSubstituteInvitations(MatchRequest match, UserId absentPlayerId) {
    Set<String> currentPlayerIds =
        match.getPlayers().stream()
            .map(p -> p.getPlayerId().value().toString())
            .collect(Collectors.toSet());

    Instant now = clock.instant();
    List<MatchInvitation> invitations =
        eligiblePlayerPort
            .findEligiblePlayers(
                match.getSearchCenter(),
                match.getSearchRadiusKm(),
                match.getSkillLevel(),
                absentPlayerId.value().toString())
            .stream()
            .filter(p -> !currentPlayerIds.contains(p.userId()))
            .map(
                p ->
                    MatchInvitation.createFreeSubstitute(
                        match.getId().value(), UUID.fromString(p.userId()), p.email(), now))
            .toList();

    if (!invitations.isEmpty()) {
      invitationRepository.saveAll(invitations);
      try {
        notificationPort.sendMatchInvitations(match, invitations);
      } catch (Exception e) {
        log.warn(
            "Failed to send substitute invitations for match {}: {}",
            match.getId().value(),
            e.getMessage());
      }
      log.info(
          "Sent {} substitute invitations for match {}", invitations.size(), match.getId().value());
    }
  }

  private void notifyRemainingPlayers(MatchRequest match, UserId absentPlayerId) {
    Set<UserId> remainingIds =
        match.getPlayers().stream()
            .map(p -> p.getPlayerId())
            .filter(id -> !id.equals(absentPlayerId))
            .collect(Collectors.toSet());

    if (remainingIds.isEmpty()) return;

    try {
      Map<UserId, String> emails = userRepository.findEmailsByIds(remainingIds);
      List<String> remainingEmails = List.copyOf(emails.values());
      if (!remainingEmails.isEmpty()) {
        notificationPort.notifyPlayerAbsence(match, remainingEmails);
      }
    } catch (Exception e) {
      log.warn(
          "Failed to send absence notifications for match {}: {}",
          match.getId().value(),
          e.getMessage());
    }
  }
}
