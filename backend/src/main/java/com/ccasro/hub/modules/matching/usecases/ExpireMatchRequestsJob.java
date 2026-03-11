package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.infrastructure.config.MatchingProperties;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpireMatchRequestsJob {

  private final MatchRequestRepositoryPort matchRepository;
  private final MatchRequestExpirationService expirationService;
  private final MatchingProperties matchingProperties;
  private final Clock clock;

  @Scheduled(fixedDelayString = "PT5M")
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
        expirationService.expireMatch(match);
      } catch (Exception e) {
        log.error("Error expiring match request {}: {}", match.getId().value(), e.getMessage());
      }
    }
  }

  private void cancelUnpaidOrganizerMatches() {
    var deadline = clock.instant().minus(matchingProperties.getOrganizerPaymentWindow());
    List<MatchRequest> unpaid = matchRepository.findAwaitingPaymentExpired(deadline);
    if (unpaid.isEmpty()) return;

    log.info("Cancelling {} match requests where organizer did not pay in time", unpaid.size());
    for (MatchRequest match : unpaid) {
      try {
        expirationService.cancelDueToPaymentTimeout(match);
      } catch (Exception e) {
        log.error(
            "Error cancelling unpaid match request {}: {}", match.getId().value(), e.getMessage());
      }
    }
  }
}
