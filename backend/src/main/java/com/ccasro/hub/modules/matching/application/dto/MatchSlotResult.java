package com.ccasro.hub.modules.matching.application.dto;

import java.time.LocalTime;
import java.util.UUID;

public record MatchSlotResult(
    UUID resourceId,
    String resourceName,
    String resourceType,
    UUID venueId,
    String venueName,
    String venueCity,
    double venueLatitude,
    double venueLongitude,
    double distanceKm,
    LocalTime startTime,
    LocalTime endTime,
    double price,
    String currency,
    int eligiblePlayersNearby) {}
