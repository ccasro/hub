package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.events.MatchFullEvent;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchCompletionHandler {

  private final BookingRepositoryPort bookingRepository;
  private final UserProfileRepositoryPort userRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  public void onMatchFull(MatchRequest matchRequest) {
    confirmBooking(matchRequest);
    publishMatchFullEvent(matchRequest);
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
              .filter(Objects::nonNull)
              .toList();

      if (!emails.isEmpty()) {
        eventPublisher.publishEvent(new MatchFullEvent(matchRequest, emails));
      }
    } catch (Exception e) {
      log.warn("Failed to queue match full notifications: {}", e.getMessage());
    }
  }
}
