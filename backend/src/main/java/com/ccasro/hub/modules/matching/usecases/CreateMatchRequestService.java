package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.infrastructure.config.MatchingProperties;
import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.exception.SlotNotAvailableException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.application.dto.CreateMatchRequestCommand;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.exception.MatchCreationCooldownException;
import com.ccasro.hub.modules.matching.domain.exception.PlayerTimeConflictException;
import com.ccasro.hub.modules.matching.domain.exception.TooManyActiveMatchesException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchPenaltyPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.resource.domain.ports.out.SlotAvailabilityPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import com.ccasro.hub.shared.domain.MoneyUtils;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Set;
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
  private final PaymentPort paymentPort;
  private final PaymentRepositoryPort paymentRepository;
  private final UserProfileRepositoryPort userRepository;
  private final MatchPenaltyPort matchPenaltyPort;
  private final SlotAvailabilityPort slotAvailabilityPort;
  private final MatchingProperties matchingProperties;
  private final Clock clock;

  @Transactional
  public MatchRequest execute(CreateMatchRequestCommand cmd) {

    // Check cancellation cooldown: read-only, cooldown threshold computed from the injected clock
    long hoursLeft = matchPenaltyPort.getCooldownHoursRemaining(cmd.organizerId());
    if (hoursLeft > 0) {
      throw new MatchCreationCooldownException(hoursLeft);
    }

    // Check active match limit. The slot uniqueness constraint in the booking table
    // acts as a safety net in the unlikely event of a concurrent race.
    long activeCount = matchRepository.countActiveByOrganizer(cmd.organizerId());
    if (activeCount >= matchingProperties.getMaxConcurrentMatches()) {
      throw new TooManyActiveMatchesException();
    }

    // Check that the organizer has no active match that overlaps with the requested time slot
    checkNoTimeConflict(
        cmd.organizerId(), cmd.bookingDate(), cmd.startTime(), cmd.slotDurationMinutes());

    var availableSlots =
        slotAvailabilityPort.findAvailableSlots(
            cmd.resourceId().value(), cmd.bookingDate(), cmd.slotDurationMinutes());

    var slotLite =
        availableSlots.stream()
            .filter(s -> s.startTime().equals(cmd.startTime()))
            .findFirst()
            .orElseThrow(SlotNotAvailableException::new);

    LocalTime endTime = cmd.startTime().plusMinutes(cmd.slotDurationMinutes());
    BigDecimal totalPrice = BigDecimal.valueOf(slotLite.price());
    List<BigDecimal> shares = MoneyUtils.split(totalPrice, cmd.format().getMaxPlayers());
    BigDecimal organizerShare = shares.get(0);
    BigDecimal playerShare = shares.get(shares.size() - 1);

    LocalDateTime matchStart = LocalDateTime.of(cmd.bookingDate(), cmd.startTime());
    if (LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)
        .isAfter(matchStart.minus(matchingProperties.getMinHoursBeforeCreation()))) {
      throw new IllegalArgumentException(
          "Match must be created at least "
              + matchingProperties.getMinHoursBeforeCreation().toHours()
              + " hours before the scheduled start time");
    }

    Instant matchExpiresAt =
        matchStart
            .minus(matchingProperties.getMatchExpirationHoursBefore())
            .toInstant(ZoneOffset.UTC);

    Booking booking =
        Booking.createForMatch(
            cmd.resourceId(),
            cmd.organizerId(),
            cmd.bookingDate(),
            new SlotRange(cmd.startTime(), endTime),
            totalPrice,
            slotLite.currency(),
            matchExpiresAt,
            clock);
    Booking savedBooking = bookingRepository.save(booking);

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
            playerShare,
            matchExpiresAt,
            clock);
    matchRepository.save(matchRequest);

    String organizerEmail =
        userRepository.findEmailsByIds(Set.of(cmd.organizerId())).get(cmd.organizerId());
    PaymentPort.PaymentIntent intent =
        paymentPort.createPaymentIntent(
            organizerShare, slotLite.currency(), savedBooking.getId(), organizerEmail);
    paymentRepository.save(
        Payment.createForPlayer(
            savedBooking.getId(),
            cmd.organizerId(),
            intent.paymentIntentId(),
            organizerShare,
            slotLite.currency(),
            clock));

    return matchRequest;
  }

  private void checkNoTimeConflict(
      UserId organizerId, LocalDate bookingDate, LocalTime startTime, int slotDurationMinutes) {
    LocalTime endTime = startTime.plusMinutes(slotDurationMinutes);
    boolean conflict =
        matchRepository.findActiveByPlayerAndDate(organizerId, bookingDate).stream()
            .anyMatch(
                existing -> {
                  LocalTime existingEnd =
                      existing.getStartTime().plusMinutes(existing.getSlotDurationMinutes());
                  return startTime.isBefore(existingEnd)
                      && existing.getStartTime().isBefore(endTime);
                });
    if (conflict) throw new PlayerTimeConflictException();
  }
}
