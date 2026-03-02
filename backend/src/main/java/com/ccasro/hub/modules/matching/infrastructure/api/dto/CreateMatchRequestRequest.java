package com.ccasro.hub.modules.matching.infrastructure.api.dto;

import com.ccasro.hub.modules.matching.domain.MatchFormat;
import com.ccasro.hub.modules.matching.domain.MatchSkillLevel;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateMatchRequestRequest(
    @NotNull UUID resourceId,
    @NotNull LocalDate bookingDate,
    @NotNull LocalTime startTime,
    @NotNull Integer slotDurationMinutes,
    @NotNull MatchFormat format,
    @NotNull MatchSkillLevel skillLevel,
    String customMessage,
    @NotNull Double searchLat,
    @NotNull Double searchLng,
    double searchRadiusKm) {}
