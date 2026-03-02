package com.ccasro.hub.modules.matching.infrastructure.notification;

import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.PlayerTeam;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchNotificationPort;
import jakarta.mail.internet.MimeMessage;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
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
public class MatchNotificationAdapter implements MatchNotificationPort {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final ResourceReadPort resourceReadPort;
  private final VenueReadPort venueReadPort;

  @Value("${app.mail.from:noreply@sportshub.com}")
  private String from;

  @Value("${app.frontend.url:http://localhost:3000}")
  private String frontendUrl;

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

  @Override
  public void sendMatchInvitations(MatchRequest matchRequest, List<String> playerEmails) {
    var enriched = enrich(matchRequest);
    if (enriched == null) return;

    String joinUrl = frontendUrl + "/match/join/" + matchRequest.getInvitationToken().value();
    String expiresAt =
        matchRequest
            .getExpiresAt()
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

    String formatLabel = matchRequest.getFormat().name().equals("ONE_VS_ONE") ? "1 vs 1" : "2 vs 2";

    String skillLabel =
        switch (matchRequest.getSkillLevel()) {
          case BEGINNER -> "Principiante";
          case INTERMEDIATE -> "Intermedio";
          case ADVANCED -> "Avanzado";
          case ANY -> "Cualquier nivel";
        };

    for (String email : playerEmails) {
      try {
        Context ctx = new Context(Locale.forLanguageTag("es"));
        ctx.setVariable("playerName", email.split("@")[0]);
        ctx.setVariable("organizerName", "Un jugador");
        ctx.setVariable("resourceName", enriched.resourceName());
        ctx.setVariable("venueName", enriched.venueName());
        ctx.setVariable("bookingDate", matchRequest.getBookingDate().format(DATE_FMT));
        ctx.setVariable("startTime", matchRequest.getStartTime().format(TIME_FMT));
        ctx.setVariable(
            "endTime",
            matchRequest
                .getStartTime()
                .plusMinutes(matchRequest.getSlotDurationMinutes())
                .format(TIME_FMT));
        ctx.setVariable("format", formatLabel);
        ctx.setVariable("skillLevel", skillLabel);
        ctx.setVariable("availableSlots", matchRequest.availableSlots());
        ctx.setVariable("customMessage", matchRequest.getCustomMessage());
        ctx.setVariable("joinUrl", joinUrl);
        ctx.setVariable("expiresAt", expiresAt);

        sendHtmlEmail(
            email, "⚔️ Te invitan a un partido — SportsHub", "email/match-invitation", ctx);
      } catch (Exception e) {
        log.error("Failed to send match invitation to {}: {}", email, e.getMessage());
      }
    }
  }

  @Override
  public void notifyMatchFull(MatchRequest matchRequest, List<String> playerEmails) {
    var enriched = enrich(matchRequest);
    if (enriched == null) return;

    List<String> team1 =
        matchRequest.getPlayers().stream()
            .filter(p -> p.getTeam() == PlayerTeam.TEAM_1)
            .map(p -> p.getPlayerId().value().toString())
            .collect(Collectors.toList());

    List<String> team2 =
        matchRequest.getPlayers().stream()
            .filter(p -> p.getTeam() == PlayerTeam.TEAM_2)
            .map(p -> p.getPlayerId().value().toString())
            .collect(Collectors.toList());

    String matchUrl = frontendUrl + "/match/" + matchRequest.getId().value();

    for (String email : playerEmails) {
      try {
        Context ctx = new Context(Locale.forLanguageTag("es"));
        ctx.setVariable("playerName", email.split("@")[0]);
        ctx.setVariable("resourceName", enriched.resourceName());
        ctx.setVariable("venueName", enriched.venueName());
        ctx.setVariable("bookingDate", matchRequest.getBookingDate().format(DATE_FMT));
        ctx.setVariable("startTime", matchRequest.getStartTime().format(TIME_FMT));
        ctx.setVariable(
            "endTime",
            matchRequest
                .getStartTime()
                .plusMinutes(matchRequest.getSlotDurationMinutes())
                .format(TIME_FMT));
        ctx.setVariable("team1Players", team1);
        ctx.setVariable("team2Players", team2);
        ctx.setVariable("dashboardUrl", matchUrl);

        sendHtmlEmail(email, "🎉 ¡Partido completo! — SportsHub", "email/match-full", ctx);
      } catch (Exception e) {
        log.error("Failed to send match full email to {}: {}", email, e.getMessage());
      }
    }
  }

  @Override
  public void notifyMatchCancelled(MatchRequest matchRequest, List<String> playerEmails) {
    var enriched = enrich(matchRequest);
    if (enriched == null) return;

    for (String email : playerEmails) {
      try {
        Context ctx = new Context(Locale.forLanguageTag("es"));
        ctx.setVariable("playerName", email.split("@")[0]);
        ctx.setVariable("resourceName", enriched.resourceName());
        ctx.setVariable("venueName", enriched.venueName());
        ctx.setVariable("bookingDate", matchRequest.getBookingDate().format(DATE_FMT));
        ctx.setVariable("startTime", matchRequest.getStartTime().format(TIME_FMT));
        ctx.setVariable(
            "endTime",
            matchRequest
                .getStartTime()
                .plusMinutes(matchRequest.getSlotDurationMinutes())
                .format(TIME_FMT));
        ctx.setVariable("searchUrl", frontendUrl + "/match/search");

        sendHtmlEmail(email, "❌ Partido cancelado — SportsHub", "email/match-cancelled", ctx);
      } catch (Exception e) {
        log.error("Failed to send match cancelled email to {}: {}", email, e.getMessage());
      }
    }
  }

  // ── Helpers ───────────────────────────────────────────────────

  private record EnrichedNames(String resourceName, String venueName) {}

  private EnrichedNames enrich(MatchRequest matchRequest) {
    try {
      var resource =
          resourceReadPort
              .findLiteByIds(List.of(matchRequest.getResourceId().value()))
              .get(matchRequest.getResourceId().value());

      var venue =
          resource != null
              ? venueReadPort.findLiteByIds(Set.of(resource.venueId())).get(resource.venueId())
              : null;

      return new EnrichedNames(
          resource != null ? resource.name() : "Pista", venue != null ? venue.name() : "Club");
    } catch (Exception e) {
      log.error("Failed to enrich match notification: {}", e.getMessage());
      return new EnrichedNames("Pista", "Club");
    }
  }

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
