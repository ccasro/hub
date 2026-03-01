package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.infrastructure.config.BookingProperties;
import com.ccasro.hub.modules.booking.application.dto.CreateBookingCommand;
import com.ccasro.hub.modules.booking.application.dto.CreateBookingResult;
import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.exception.SlotNotAvailableException;
import com.ccasro.hub.modules.booking.domain.ports.out.*;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBookingService {

  private final BookingRepositoryPort bookingRepository;
  private final BookingProperties bookingProperties;
  private final ResourceValidationPort resourceValidation;
  private final UserProfileRepositoryPort userRepository;
  private final PaymentPort paymentPort;
  private final PaymentRepositoryPort paymentRepository;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  @CacheEvict(value = "slots", key = "#cmd.resourceId().value() + '_' + #cmd.bookingDate()")
  public CreateBookingResult execute(CreateBookingCommand cmd) {
    UserId playerId = currentUser.getUserId();

    ResourceValidationPort.ResourceInfo resource =
        resourceValidation.findActiveResource(cmd.resourceId(), cmd.bookingDate(), cmd.startTime());

    LocalTime endTime = cmd.startTime().plusMinutes(resource.slotDuration().minutes());
    SlotRange slot = new SlotRange(cmd.startTime(), endTime);

    if (bookingRepository.existsConfirmedBooking(
        cmd.resourceId(), cmd.bookingDate(), cmd.startTime()))
      throw new SlotNotAvailableException();

    BigDecimal price = resource.priceForSlot();
    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalStateException("This slot does not have a price set");

    Booking booking =
        Booking.create(
            cmd.resourceId(),
            playerId,
            cmd.bookingDate(),
            slot,
            price,
            resource.currency(),
            bookingProperties.getPaymentHoldDuration(),
            clock);
    Booking saved = bookingRepository.save(booking);

    String playerEmail =
        userRepository.findById(playerId).map(p -> p.getEmail().value()).orElse(null);

    PaymentPort.PaymentIntent intent =
        paymentPort.createPaymentIntent(price, resource.currency(), saved.getId(), playerEmail);

    Payment payment =
        Payment.create(saved.getId(), intent.paymentIntentId(), price, resource.currency(), clock);
    paymentRepository.save(payment);

    return new CreateBookingResult(saved, intent.clientSecret());
  }
}
