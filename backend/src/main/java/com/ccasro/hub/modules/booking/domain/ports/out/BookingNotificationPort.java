package com.ccasro.hub.modules.booking.domain.ports.out;

import com.ccasro.hub.modules.booking.domain.Booking;

public interface BookingNotificationPort {
  void notifyBookingConfirmed(Booking booking, String playerEmail);

  void notifyBookingCancelled(Booking booking, String playerEmail);

  void notifyBookingExpired(Booking booking, String playerEmail);
}
