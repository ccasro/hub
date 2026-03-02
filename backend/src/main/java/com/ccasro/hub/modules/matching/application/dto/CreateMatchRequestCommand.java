package com.ccasro.hub.modules.matching.application.dto;

import com.ccasro.hub.modules.matching.domain.MatchFormat;
import com.ccasro.hub.modules.matching.domain.MatchSkillLevel;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateMatchRequestCommand(
    UserId organizerId,
    ResourceId resourceId,
    LocalDate bookingDate,
    LocalTime startTime,
    int slotDurationMinutes,
    MatchFormat format,
    MatchSkillLevel skillLevel,
    String customMessage,
    GeoPoint searchCenter,
    double searchRadiusKm) {}
