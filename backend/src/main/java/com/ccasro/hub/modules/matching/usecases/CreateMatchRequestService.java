package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.exception.SlotNotAvailableException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.matching.application.dto.CreateMatchRequestCommand;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.EligiblePlayerPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchNotificationPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.resource.domain.ports.out.SlotAvailabilityPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateMatchRequestService {

  private final MatchRequestRepositoryPort matchRepository;
  private final BookingRepositoryPort bookingRepository;
  private final SlotAvailabilityPort slotAvailabilityPort;
  private final EligiblePlayerPort eligiblePlayerPort;
  private final MatchNotificationPort notificationPort;
  private final Clock clock;

  private static final Duration MATCH_TTL = Duration.ofHours(48);

  @Transactional
  public MatchRequest execute(CreateMatchRequestCommand cmd) {

    var availableSlots =
        slotAvailabilityPort.findAvailableSlots(
            cmd.resourceId().value(), cmd.bookingDate(), cmd.slotDurationMinutes());

    boolean slotExists =
        availableSlots.stream().anyMatch(s -> s.startTime().equals(cmd.startTime()));

    if (!slotExists) throw new SlotNotAvailableException();

    var slotLite =
        availableSlots.stream()
            .filter(s -> s.startTime().equals(cmd.startTime()))
            .findFirst()
            .orElseThrow(SlotNotAvailableException::new);

    LocalTime endTime = cmd.startTime().plusMinutes(cmd.slotDurationMinutes());
    Booking booking =
        Booking.createForMatch(
            cmd.resourceId(),
            cmd.organizerId(),
            cmd.bookingDate(),
            new SlotRange(cmd.startTime(), endTime),
            BigDecimal.valueOf(slotLite.price()),
            slotLite.currency(),
            MATCH_TTL,
            clock);
    bookingRepository.save(booking);

    MatchRequest matchRequest =
        MatchRequest.create(
            cmd.organizerId(),
            cmd.resourceId(),
            cmd.bookingDate(),
            cmd.startTime(),
            cmd.slotDurationMinutes(),
            cmd.format(),
            cmd.skillLevel(),
            cmd.customMessage(),
            cmd.searchCenter(),
            cmd.searchRadiusKm(),
            clock);
    matchRepository.save(matchRequest);

    try {
      List<String> emails =
          eligiblePlayerPort
              .findEligiblePlayers(
                  cmd.searchCenter(),
                  cmd.searchRadiusKm(),
                  cmd.skillLevel(),
                  cmd.organizerId().value().toString())
              .stream()
              .filter(p -> p.matchNotificationsEnabled())
              .map(p -> p.email())
              .toList();

      if (!emails.isEmpty()) {
        notificationPort.sendMatchInvitations(matchRequest, emails);
        log.info(
            "Sent match invitations to {} players for match {}",
            emails.size(),
            matchRequest.getId().value());
      }
    } catch (Exception e) {
      log.warn("Failed to send match invitations: {}", e.getMessage());
    }

    return matchRequest;
  }
}
