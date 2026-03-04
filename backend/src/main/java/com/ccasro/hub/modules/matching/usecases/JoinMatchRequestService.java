package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.PlayerTeam;
import com.ccasro.hub.modules.matching.domain.events.MatchFullEvent;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoinMatchRequestService {

  private final MatchRequestRepositoryPort matchRepository;
  private final BookingRepositoryPort bookingRepository;
  private final UserProfileRepositoryPort userRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public MatchRequest execute(InvitationToken token, PlayerTeam team) {

    UserId playerId = currentUser.getUserId();

    MatchRequest matchRequest =
        matchRepository
            .findByInvitationToken(token)
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    matchRequest.join(playerId, team, clock);

    if (matchRequest.isFull()) {
      confirmBooking(matchRequest);
      publishMatchFullEvent(matchRequest);
    }

    matchRepository.save(matchRequest);
    return matchRequest;
  }

  private void confirmBooking(MatchRequest matchRequest) {
    bookingRepository
        .findByResourceIdAndDate(matchRequest.getResourceId(), matchRequest.getBookingDate())
        .stream()
        .filter(b -> b.getStatus() == BookingStatus.PENDING_MATCH)
        .filter(b -> b.getSlot().startTime().equals(matchRequest.getStartTime()))
        .findFirst()
        .ifPresent(
            b -> {
              b.confirmMatch(clock);
              bookingRepository.save(b);
              log.info(
                  "Booking {} confirmed for match {}",
                  b.getId().value(),
                  matchRequest.getId().value());
            });
  }

  private void publishMatchFullEvent(MatchRequest matchRequest) {
    try {
      List<String> emails =
          matchRequest.getPlayers().stream()
              .map(
                  p ->
                      userRepository
                          .findById(p.getPlayerId())
                          .map(u -> u.getEmail().value())
                          .orElse(null))
              .filter(e -> e != null)
              .toList();

      if (!emails.isEmpty()) {
        eventPublisher.publishEvent(new MatchFullEvent(matchRequest, emails));
      }
    } catch (Exception e) {
      log.warn("Failed to queue match full notifications: {}", e.getMessage());
    }
  }
}
