package com.ccasro.hub.modules.matching.application.dto;

import com.ccasro.hub.modules.matching.domain.MatchFormat;
import com.ccasro.hub.modules.matching.domain.MatchSkillLevel;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import java.time.LocalDate;
import java.time.LocalTime;

public record SearchMatchSlotsQuery(
    GeoPoint center,
    double radiusKm,
    LocalDate date,
    LocalTime startTimeFrom,
    LocalTime startTimeTo,
    int slotDurationMinutes,
    MatchFormat format,
    MatchSkillLevel skillLevel) {}
