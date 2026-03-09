package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.CancelBookingCommand;
import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.events.BookingCancelledEvent;
import com.ccasro.hub.modules.booking.domain.exception.BookingCancellationNotAllowedException;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelBookingService {

  private final BookingRepositoryPort bookingRepository;
  private final PaymentRepositoryPort paymentRepository;
  private final PaymentPort paymentPort;
  private final MatchRequestRepositoryPort matchRequestRepository;
  private final UserProfileRepositoryPort userRepository;
  private final CurrentUserProvider currentUser;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  @Transactional
  @CacheEvict(value = "slots", allEntries = true)
  public Booking execute(CancelBookingCommand cmd) {
    Booking booking =
        bookingRepository.findById(cmd.bookingId()).orElseThrow(BookingNotFoundException::new);

    if (!booking.isOwnedBy(currentUser.getUserId()))
      throw new AccessDeniedException("You are not the owner of this booking");

    matchRequestRepository
        .findActiveByResourceAndSlot(
            booking.getResourceId(), booking.getBookingDate(), booking.getSlot().startTime())
        .ifPresent(
            match -> {
              throw new BookingCancellationNotAllowedException(
                  "Esta reserva pertenece a un partido activo. Para salir del partido utiliza la opción 'Abandonar partido' o 'Cancelar partido' desde el detalle del partido.");
            });

    booking.cancel(cmd.reason(), clock);

    if (booking.isPaid()) {
      paymentRepository
          .findByBookingId(booking.getId())
          .ifPresent(
              payment -> {
                paymentPort.refund(payment.getStripePaymentIntentId());
                log.info("Refund processed for booking: {}", booking.getId().value());
              });
    }

    Booking saved = bookingRepository.save(booking);

    userRepository
        .findById(currentUser.getUserId())
        .map(p -> p.getEmail().value())
        .ifPresent(
            email ->
                eventPublisher.publishEvent(
                    new BookingCancelledEvent(saved.getId().value(), email)));

    return saved;
  }
}
