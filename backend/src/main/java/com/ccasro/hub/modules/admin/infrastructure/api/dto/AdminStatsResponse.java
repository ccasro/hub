package com.ccasro.hub.modules.admin.infrastructure.api.dto;

import java.math.BigDecimal;

public record AdminStatsResponse(
    long totalUsers,
    long totalOwners,
    long totalPlayers,
    long totalVenues,
    long activeVenues,
    long pendingVenues,
    long totalResources,
    long activeResources,
    long pendingResources,
    long pendingOwnerRequests,
    BigDecimal revenueThisMonth,
    long totalBookings) {}
