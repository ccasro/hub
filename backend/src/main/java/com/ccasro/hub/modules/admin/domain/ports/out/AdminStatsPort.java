package com.ccasro.hub.modules.admin.domain.ports.out;

import java.math.BigDecimal;

public interface AdminStatsPort {
  AdminStats fetchStats();

  record AdminStats(
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
}
