package com.ccasro.hub.modules.booking.infrastructure.email;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.events.BookingCancelledEvent;
import com.ccasro.hub.modules.booking.domain.events.BookingConfirmedEvent;
import com.ccasro.hub.modules.booking.domain.events.BookingExpiredEvent;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingNotificationPort;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventListener {

  private final BookingNotificationPort notificationPort;
  private final BookingRepositoryPort bookingRepository;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBookingConfirmed(BookingConfirmedEvent event) {
    try {
      Booking booking =
          bookingRepository
              .findById(new BookingId(event.bookingId()))
              .orElseThrow(BookingNotFoundException::new);
      notificationPort.notifyBookingConfirmed(booking, event.playerEmail());
    } catch (Exception e) {
      log.error("Failed to send booking confirmed email: {}", e.getMessage());
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBookingCancelled(BookingCancelledEvent event) {
    try {
      Booking booking =
          bookingRepository
              .findById(new BookingId(event.bookingId()))
              .orElseThrow(BookingNotFoundException::new);
      notificationPort.notifyBookingCancelled(booking, event.playerEmail());
    } catch (Exception e) {
      log.error("Failed to send booking cancelled email: {}", e.getMessage());
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBookingExpired(BookingExpiredEvent event) {
    try {
      Booking booking =
          bookingRepository
              .findById(new BookingId(event.bookingId()))
              .orElseThrow(BookingNotFoundException::new);
      notificationPort.notifyBookingExpired(booking, event.playerEmail());
    } catch (Exception e) {
      log.error("Failed to send booking expired email: {}", e.getMessage());
    }
  }
}
