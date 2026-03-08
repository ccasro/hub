package com.ccasro.hub.modules.matching.infrastructure.api.dto;

import com.ccasro.hub.modules.matching.usecases.GetMyInvitationsService.MatchInvitationView;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record MatchInvitationResponse(
    UUID id,
    UUID matchRequestId,
    String status,
    Instant sentAt,
    Instant respondedAt,
    LocalDate bookingDate,
    LocalTime startTime,
    LocalTime endTime,
    String format,
    String skillLevel,
    String matchStatus,
    int availableSlots,
    BigDecimal pricePerPlayer,
    String resourceName,
    String venueName,
    String venueCity) {

  public static MatchInvitationResponse from(MatchInvitationView view) {
    var inv = view.invitation();
    var match = view.matchRequest();

    return new MatchInvitationResponse(
        inv.getId(),
        inv.getMatchRequestId(),
        inv.getStatus().name(),
        inv.getSentAt(),
        inv.getRespondedAt(),
        match != null ? match.getBookingDate() : null,
        match != null ? match.getStartTime() : null,
        match != null ? match.getStartTime().plusMinutes(match.getSlotDurationMinutes()) : null,
        match != null ? match.getFormat().name() : null,
        match != null ? match.getSkillLevel().name() : null,
        match != null ? match.getStatus().name() : null,
        match != null ? match.availableSlots() : 0,
        match != null ? match.getPricePerPlayer() : null,
        view.resourceName(),
        view.venueName(),
        view.venueCity());
  }
}
