package com.ccasro.hub.modules.matching.infrastructure.notification;

import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.events.MatchFullEvent;
import com.ccasro.hub.modules.matching.domain.events.MatchInvitationsEvent;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchNotificationPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchEventListener {

  private final MatchNotificationPort notificationPort;
  private final MatchInvitationRepositoryPort invitationRepository;
  private final MatchRequestRepositoryPort matchRepository;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onMatchInvitations(MatchInvitationsEvent event) {
    try {
      List<MatchInvitation> invitations =
          invitationRepository.findByMatchRequestId(event.matchRequestId());

      if (invitations.isEmpty()) return;

      matchRepository
          .findById(new MatchRequestId(event.matchRequestId()))
          .ifPresent(
              matchRequest -> {
                notificationPort.sendMatchInvitations(matchRequest, invitations);
                log.info(
                    "Sent match invitations to {} players for match {}",
                    invitations.size(),
                    event.matchRequestId());
              });
    } catch (Exception e) {
      log.error("Failed to send match invitations: {}", e.getMessage());
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onMatchFull(MatchFullEvent event) {
    try {
      notificationPort.notifyMatchFull(event.matchRequest(), event.emails());
      log.info(
          "Sent match full notifications to {} players for match {}",
          event.emails().size(),
          event.matchRequest().getId().value());
    } catch (Exception e) {
      log.error("Failed to send match full notifications: {}", e.getMessage());
    }
  }
}
