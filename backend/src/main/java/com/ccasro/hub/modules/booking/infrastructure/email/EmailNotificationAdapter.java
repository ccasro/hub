package com.ccasro.hub.modules.booking.infrastructure.email;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements BookingNotificationPort {

  private final JavaMailSender mailSender;

  @Value("${app.mail.from:noreply@padelhub.com}")
  private String from;

  @Override
  public void notifyBookingConfirmed(Booking booking, String playerEmail) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(from);
      message.setTo(playerEmail);
      message.setSubject("Booking Confirmed - Padel Hub");
      message.setText(
          """
               Your booking has been confirmed.

               Date: %s
               Time: %s - %s
               Price: %s %s

               See you son!
               """
              .formatted(
                  booking.getBookingDate(),
                  booking.getSlot().startTime(),
                  booking.getSlot().endTime(),
                  booking.getPricePaid(),
                  booking.getCurrency()));
      mailSender.send(message);
    } catch (Exception e) {
      log.error("Failed to send confirmation email to {}: {}", playerEmail, e.getMessage());
    }
  }

  @Override
  public void notifyBookingCancelled(Booking booking, String playerEmail) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(from);
      message.setTo(playerEmail);
      message.setSubject("Booking Cancelled - Padel Hub");
      message.setText(
          """
                Your booking has been cancelled.

                Date: %s
                Time: %s - %s
                Reason: %s
                """
              .formatted(
                  booking.getBookingDate(),
                  booking.getSlot().startTime(),
                  booking.getSlot().endTime(),
                  booking.getCancelReason() != null
                      ? booking.getCancelReason()
                      : "No reason provided"));
      mailSender.send(message);
    } catch (Exception e) {
      log.error("Failed to send cancellation email to {}: {}", playerEmail, e.getMessage());
    }
  }

  @Override
  public void notifyBookingExpired(Booking booking, String playerEmail) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(from);
      message.setTo(playerEmail);
      message.setSubject("Booking Expired - Padel Hub");
      message.setText(
          """
            Your booking has expired because the payment was not completed in time.

            Date: %s
            Time: %s - %s

            You can try again whenever you like.
            """
              .formatted(
                  booking.getBookingDate(),
                  booking.getSlot().startTime(),
                  booking.getSlot().endTime()));
      mailSender.send(message);
    } catch (Exception e) {
      log.error("Failed to send expiration email to {}: {}", playerEmail, e.getMessage());
    }
  }
}
