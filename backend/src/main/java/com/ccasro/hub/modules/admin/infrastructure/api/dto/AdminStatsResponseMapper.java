package com.ccasro.hub.modules.admin.infrastructure.api.dto;

import com.ccasro.hub.modules.admin.domain.ports.out.AdminStatsPort;

public class AdminStatsResponseMapper {

  private AdminStatsResponseMapper() {}

  public static AdminStatsResponse from(AdminStatsPort.AdminStats stats) {
    return new AdminStatsResponse(
        stats.totalUsers(),
        stats.totalOwners(),
        stats.totalPlayers(),
        stats.totalVenues(),
        stats.activeVenues(),
        stats.pendingVenues(),
        stats.totalResources(),
        stats.activeResources(),
        stats.pendingResources(),
        stats.pendingOwnerRequests(),
        stats.revenueThisMonth(),
        stats.totalBookings());
  }
}
