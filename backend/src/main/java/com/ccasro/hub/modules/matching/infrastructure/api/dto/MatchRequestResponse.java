package com.ccasro.hub.modules.matching.infrastructure.api.dto;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record MatchRequestResponse(
    UUID id,
    UUID resourceId,
    LocalDate bookingDate,
    LocalTime startTime,
    LocalTime endTime,
    String format,
    String skillLevel,
    String status,
    String invitationToken,
    int availableSlots,
    Instant expiresAt,
    List<MatchPlayerResponse> players) {
  public record MatchPlayerResponse(String playerId, String team, String role, Instant joinedAt) {}

  public static MatchRequestResponse from(MatchRequest m) {
    return new MatchRequestResponse(
        m.getId().value(),
        m.getResourceId().value(),
        m.getBookingDate(),
        m.getStartTime(),
        m.getStartTime().plusMinutes(m.getSlotDurationMinutes()),
        m.getFormat().name(),
        m.getSkillLevel().name(),
        m.getStatus().name(),
        m.getInvitationToken().value().toString(),
        m.availableSlots(),
        m.getExpiresAt(),
        m.getPlayers().stream()
            .map(
                p ->
                    new MatchPlayerResponse(
                        p.getPlayerId().value().toString(),
                        p.getTeam().name(),
                        p.getRole().name(),
                        p.getJoinedAt()))
            .toList());
  }
}
