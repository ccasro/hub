package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingNotificationPort;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBookingService {

  private final BookingRepositoryPort bookingRepository;
  private final BookingNotificationPort notificationPort;
  private final UserProfileRepositoryPort userRepository;
  private final Clock clock;

  @PreAuthorize("@authz.isAdmin()")
  public List<Booking> findAll(int page, int size) {
    return bookingRepository.findAll(page, size);
  }

  @Transactional
  @PreAuthorize("@authz.isAdmin()")
  public Booking cancel(BookingId id, String reason) {
    Booking booking = bookingRepository.findById(id).orElseThrow(BookingNotFoundException::new);

    booking.adminCancel(reason, clock);
    Booking saved = bookingRepository.save(booking);

    try {
      userRepository
          .findById(booking.getPlayerId())
          .map(p -> p.getEmail().value())
          .ifPresent(playerEmail -> notificationPort.notifyBookingCancelled(saved, playerEmail));
    } catch (Exception e) {
    }

    return saved;
  }
}
