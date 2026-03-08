package com.ccasro.hub.modules.matching.usecases;

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
import com.ccasro.hub.modules.matching.domain.exception.TooManyActiveMatchesException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.resource.domain.ports.out.SlotAvailabilityPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import com.ccasro.hub.shared.domain.MoneyUtils;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateMatchRequestService {

  static final int MAX_CONCURRENT_MATCHES = 2;

  private final MatchRequestRepositoryPort matchRepository;
  private final BookingRepositoryPort bookingRepository;
  private final PaymentPort paymentPort;
  private final PaymentRepositoryPort paymentRepository;
  private final UserProfileRepositoryPort userRepository;
  private final SlotAvailabilityPort slotAvailabilityPort;
  private final Clock clock;

  @Transactional
  public MatchRequest execute(CreateMatchRequestCommand cmd) {

    // Check cancellation cooldown: read-only, cooldown threshold computed from the injected clock
    long hoursLeft = userRepository.getCooldownHoursRemaining(cmd.organizerId());
    if (hoursLeft > 0) {
      throw new MatchCreationCooldownException(hoursLeft);
    }

    // Check active match limit. The slot uniqueness constraint in the booking table
    // acts as a safety net in the unlikely event of a concurrent race.
    long activeCount = matchRepository.countActiveByOrganizer(cmd.organizerId());
    if (activeCount >= MAX_CONCURRENT_MATCHES) {
      throw new TooManyActiveMatchesException();
    }

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
        .isAfter(matchStart.minusHours(48))) {
      throw new IllegalArgumentException(
          "Match must be created at least 48 hours before the scheduled start time");
    }

    Instant matchExpiresAt = matchStart.minusHours(24).toInstant(ZoneOffset.UTC);

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
            clock);
    matchRepository.save(matchRequest);

    String organizerEmail =
        userRepository.findById(cmd.organizerId()).map(u -> u.getEmail().value()).orElse(null);
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
}
