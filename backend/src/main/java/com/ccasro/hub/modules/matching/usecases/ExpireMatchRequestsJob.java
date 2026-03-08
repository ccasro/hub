package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpireMatchRequestsJob {

  private static final Duration ORGANIZER_PAYMENT_WINDOW = Duration.ofMinutes(30);

  private final MatchRequestRepositoryPort matchRepository;
  private final MatchInvitationRepositoryPort invitationRepository;
  private final MatchPlayerPaymentService matchPlayerPaymentService;
  private final Clock clock;

  @Scheduled(fixedDelayString = "PT5M")
  @Transactional
  public void execute() {
    expireOpenMatches();
    cancelUnpaidOrganizerMatches();
  }

  private void expireOpenMatches() {
    List<MatchRequest> expired = matchRepository.findOpenAndExpired();
    if (expired.isEmpty()) return;

    log.info("Expiring {} open match requests", expired.size());
    for (MatchRequest match : expired) {
      try {
        match.expire();
        matchRepository.save(match);
        invitationRepository.expireByMatchRequestId(match.getId().value(), clock.instant());
        match
            .getPlayers()
            .forEach(p -> matchPlayerPaymentService.refundPlayerPayment(match, p.getPlayerId()));
        matchPlayerPaymentService.cancelMatchBooking(match);
        log.info("Expired match request {}", match.getId().value());
      } catch (Exception e) {
        log.error("Error expiring match request {}: {}", match.getId().value(), e.getMessage());
      }
    }
  }

  private void cancelUnpaidOrganizerMatches() {
    var deadline = clock.instant().minus(ORGANIZER_PAYMENT_WINDOW);
    List<MatchRequest> unpaid = matchRepository.findAwaitingPaymentExpired(deadline);
    if (unpaid.isEmpty()) return;

    log.info("Cancelling {} match requests where organizer did not pay in time", unpaid.size());
    for (MatchRequest match : unpaid) {
      try {
        match.cancelDueToPaymentTimeout();
        matchRepository.save(match);
        invitationRepository.expireByMatchRequestId(match.getId().value(), clock.instant());
        matchPlayerPaymentService.refundPlayerPayment(match, match.getOrganizerId());
        matchPlayerPaymentService.cancelMatchBooking(match);
        log.info("Cancelled unpaid match request {}", match.getId().value());
      } catch (Exception e) {
        log.error(
            "Error cancelling unpaid match request {}: {}", match.getId().value(), e.getMessage());
      }
    }
  }
}
