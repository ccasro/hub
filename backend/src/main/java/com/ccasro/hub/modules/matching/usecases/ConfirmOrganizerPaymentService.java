package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.events.MatchInvitationsEvent;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.exception.NotMatchOrganizerException;
import com.ccasro.hub.modules.matching.domain.ports.out.EligiblePlayerPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmOrganizerPaymentService {

  private final MatchRequestRepositoryPort matchRepository;
  private final BookingRepositoryPort bookingRepository;
  private final PaymentRepositoryPort paymentRepository;
  private final EligiblePlayerPort eligiblePlayerPort;
  private final MatchInvitationRepositoryPort invitationRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  @Transactional
  public MatchRequest execute(UUID matchId, UserId currentUserId) {

    MatchRequest match =
        matchRepository
            .findById(new MatchRequestId(matchId))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    if (!match.getOrganizerId().equals(currentUserId)) {
      throw new NotMatchOrganizerException();
    }

    if (!match.isAwaitingOrganizerPayment()) {
      throw new IllegalStateException("Match is not awaiting organizer payment");
    }

    Booking booking =
        bookingRepository
            .findByResourceIdAndDate(match.getResourceId(), match.getBookingDate())
            .stream()
            .filter(b -> b.getStatus() == BookingStatus.PENDING_MATCH)
            .filter(b -> b.getSlot().startTime().equals(match.getStartTime()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No booking found for match"));

    Payment payment =
        paymentRepository
            .findByBookingIdAndPlayerId(booking.getId(), match.getOrganizerId())
            .orElseThrow(
                () -> new IllegalStateException("No organizer payment record found for match"));

    if (payment.getStatus() == PaymentStatus.PAID) {
      throw new IllegalStateException("Organizer payment has already been confirmed");
    }

    payment.markAsPaid(clock);
    paymentRepository.save(payment);

    match.openForPlayers();
    matchRepository.save(match);

    sendInvitations(match);

    return match;
  }

  private void sendInvitations(MatchRequest match) {
    Instant now = clock.instant();
    List<MatchInvitation> invitations =
        eligiblePlayerPort
            .findEligiblePlayers(
                match.getSearchCenter(),
                match.getSearchRadiusKm(),
                match.getSkillLevel(),
                match.getOrganizerId().value().toString())
            .stream()
            .map(
                p ->
                    MatchInvitation.create(
                        match.getId().value(), UUID.fromString(p.userId()), p.email(), now))
            .toList();

    if (!invitations.isEmpty()) {
      invitationRepository.saveAll(invitations);
      log.info("Saved {} invitations for match {}", invitations.size(), match.getId().value());
    }

    try {
      if (!invitations.isEmpty()) {
        eventPublisher.publishEvent(new MatchInvitationsEvent(match.getId().value()));
      }
    } catch (Exception e) {
      log.warn("Failed to publish match invitations event: {}", e.getMessage());
    }
  }
}
