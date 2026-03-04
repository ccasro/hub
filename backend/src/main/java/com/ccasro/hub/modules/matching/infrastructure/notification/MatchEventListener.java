package com.ccasro.hub.modules.matching.infrastructure.notification;

import com.ccasro.hub.modules.matching.domain.events.MatchFullEvent;
import com.ccasro.hub.modules.matching.domain.events.MatchInvitationsEvent;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchNotificationPort;
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

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onMatchInvitations(MatchInvitationsEvent event) {
    try {
      notificationPort.sendMatchInvitations(event.matchRequest(), event.emails());
      log.info(
          "Sent match invitations to {} players for match {}",
          event.emails().size(),
          event.matchRequest().getId().value());
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
