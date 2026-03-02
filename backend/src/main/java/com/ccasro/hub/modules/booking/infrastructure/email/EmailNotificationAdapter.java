package com.ccasro.hub.modules.booking.infrastructure.email;

import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingNotificationPort;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements BookingNotificationPort {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final ResourceReadPort resourceReadPort;
  private final VenueReadPort venueReadPort;

  @Value("${app.mail.from:noreply@sportshub.com}")
  private String from;

  @Value("${app.frontend.url:http://localhost:3000}")
  private String frontendUrl;

  @Override
  public void notifyBookingConfirmed(Booking booking, String playerEmail) {
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

      sendHtmlEmail(playerEmail, "Reserva confirmada — SportsHub", "email/booking-confirmed", ctx);
    } catch (Exception e) {
      log.error("Failed to send booking confirmed email to {}: {}", playerEmail, e.getMessage());
    }
  }

  @Override
  public void notifyBookingCancelled(Booking booking, String playerEmail) {
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

      sendHtmlEmail(playerEmail, "Reserva cancelada — SportsHub", "email/booking-cancelled", ctx);
    } catch (Exception e) {
      log.error("Failed to send booking cancelled email to {}: {}", playerEmail, e.getMessage());
    }
  }

  @Override
  public void notifyBookingExpired(Booking booking, String playerEmail) {
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

      sendHtmlEmail(playerEmail, "Reserva expirada — SportsHub", "email/booking-expired", ctx);
    } catch (Exception e) {
      log.error("Failed to send booking expired email to {}: {}", playerEmail, e.getMessage());
    }
  }

  // ── Helper ────────────────────────────────────────────────────

  private void sendHtmlEmail(String to, String subject, String templateName, Context ctx)
      throws Exception {

    String html = templateEngine.process(templateName, ctx);
    MimeMessage mime = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
    helper.setFrom(from);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(html, true);
    mailSender.send(mime);
  }
}
