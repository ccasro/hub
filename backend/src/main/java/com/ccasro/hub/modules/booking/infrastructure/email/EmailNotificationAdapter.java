package com.ccasro.hub.modules.booking.infrastructure.email;

import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingNotificationPort;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements BookingNotificationPort {

  private final TemplateEngine templateEngine;
  private final ResourceReadPort resourceReadPort;
  private final VenueReadPort venueReadPort;
  private final BrevoEmailSender brevoEmailSender;

  @Value("${app.frontend.url:http://localhost:3000}")
  private String frontendUrl;

  @Override
  public void notifyBookingConfirmed(Booking booking, String playerEmail) {
    log.info("Sending booking confirmed email to {}", playerEmail);
    try {
      var resource =
          resourceReadPort
              .findLiteByIds(List.of(booking.getResourceId().value()))
              .get(booking.getResourceId().value());

      var venue =
          resource != null
              ? venueReadPort.findLiteByIds(Set.of(resource.venueId())).get(resource.venueId())
              : null;

      Context ctx = new Context(Locale.forLanguageTag("es"));
      ctx.setVariable("playerName", playerEmail.split("@")[0]);
      ctx.setVariable("resourceName", resource != null ? resource.name() : "Pista");
      ctx.setVariable("venueName", venue != null ? venue.name() : "Club");
      ctx.setVariable("bookingDate", booking.getBookingDate().toString());
      ctx.setVariable("startTime", booking.getSlot().startTime().toString());
      ctx.setVariable("endTime", booking.getSlot().endTime().toString());
      ctx.setVariable("price", booking.getPricePaid().toPlainString());
      ctx.setVariable("currency", booking.getCurrency());
      ctx.setVariable("dashboardUrl", frontendUrl + "/dashboard/bookings");

      String html = templateEngine.process("email/booking-confirmed", ctx);
      brevoEmailSender.send(playerEmail, "Reserva confirmada — SportsHub", html);
      log.info("Email sent successfully to {}", playerEmail);
    } catch (Exception e) {
      log.error("Failed to send booking confirmed email to {}: {}", playerEmail, e.getMessage(), e);
    }
  }

  @Override
  public void notifyBookingCancelled(Booking booking, String playerEmail) {
    log.info("Sending booking cancelled email to {}", playerEmail);
    try {
      var resource =
          resourceReadPort
              .findLiteByIds(List.of(booking.getResourceId().value()))
              .get(booking.getResourceId().value());

      var venue =
          resource != null
              ? venueReadPort.findLiteByIds(Set.of(resource.venueId())).get(resource.venueId())
              : null;

      Context ctx = new Context(Locale.forLanguageTag("es"));
      ctx.setVariable("playerName", playerEmail.split("@")[0]);
      ctx.setVariable("resourceName", resource != null ? resource.name() : "Pista");
      ctx.setVariable("venueName", venue != null ? venue.name() : "Club");
      ctx.setVariable("bookingDate", booking.getBookingDate().toString());
      ctx.setVariable("startTime", booking.getSlot().startTime().toString());
      ctx.setVariable("endTime", booking.getSlot().endTime().toString());
      ctx.setVariable("cancelReason", booking.getCancelReason());
      ctx.setVariable("dashboardUrl", frontendUrl + "/dashboard");

      String html = templateEngine.process("email/booking-cancelled", ctx);
      brevoEmailSender.send(playerEmail, "Reserva cancelada — SportsHub", html);
      log.info("Email sent successfully to {}", playerEmail);
    } catch (Exception e) {
      log.error("Failed to send booking cancelled email to {}: {}", playerEmail, e.getMessage(), e);
    }
  }

  @Override
  public void notifyBookingExpired(Booking booking, String playerEmail) {
    log.info("Sending booking expired email to {}", playerEmail);
    try {
      var resource =
          resourceReadPort
              .findLiteByIds(List.of(booking.getResourceId().value()))
              .get(booking.getResourceId().value());

      var venue =
          resource != null
              ? venueReadPort.findLiteByIds(Set.of(resource.venueId())).get(resource.venueId())
              : null;

      Context ctx = new Context(Locale.forLanguageTag("es"));
      ctx.setVariable("playerName", playerEmail.split("@")[0]);
      ctx.setVariable("resourceName", resource != null ? resource.name() : "Pista");
      ctx.setVariable("venueName", venue != null ? venue.name() : "Club");
      ctx.setVariable("bookingDate", booking.getBookingDate().toString());
      ctx.setVariable("startTime", booking.getSlot().startTime().toString());
      ctx.setVariable("endTime", booking.getSlot().endTime().toString());
      ctx.setVariable("dashboardUrl", frontendUrl + "/dashboard");

      String html = templateEngine.process("email/booking-expired", ctx);
      brevoEmailSender.send(playerEmail, "Reserva expirada — SportsHub", html);
      log.info("Email sent successfully to {}", playerEmail);
    } catch (Exception e) {
      log.error("Failed to send booking expired email to {}: {}", playerEmail, e.getMessage(), e);
    }
  }
}
