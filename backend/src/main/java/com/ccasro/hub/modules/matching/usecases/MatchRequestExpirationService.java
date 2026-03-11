package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchRequestExpirationService {

  private final MatchRequestRepositoryPort matchRepository;
  private final MatchInvitationRepositoryPort invitationRepository;
  private final MatchPlayerPaymentService matchPlayerPaymentService;
  private final Clock clock;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void expireMatch(MatchRequest match) {
    match.expire();
    matchRepository.save(match);
    invitationRepository.expireByMatchRequestId(match.getId().value(), clock.instant());
    match
        .getPlayers()
        .forEach(p -> matchPlayerPaymentService.refundPlayerPayment(match, p.getPlayerId()));
    matchPlayerPaymentService.cancelMatchBooking(match);
    log.info("Expired match request {}", match.getId().value());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void cancelDueToPaymentTimeout(MatchRequest match) {
    match.cancelDueToPaymentTimeout();
    matchRepository.save(match);
    invitationRepository.expireByMatchRequestId(match.getId().value(), clock.instant());
    matchPlayerPaymentService.refundPlayerPayment(match, match.getOrganizerId());
    matchPlayerPaymentService.cancelMatchBooking(match);
    log.info("Cancelled unpaid match request {}", match.getId().value());
  }
}
