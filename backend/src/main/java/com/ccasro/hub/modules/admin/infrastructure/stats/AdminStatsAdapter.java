package com.ccasro.hub.modules.admin.infrastructure.stats;

import com.ccasro.hub.modules.admin.domain.ports.out.AdminStatsPort;
import com.ccasro.hub.modules.booking.infrastructure.persistence.BookingJpaRepository;
import com.ccasro.hub.modules.iam.domain.ports.out.UserStatsPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import com.ccasro.hub.modules.resource.infrastructure.persistence.ResourceJpaRepository;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import com.ccasro.hub.modules.venue.infrastructure.persistence.VenueJpaRepository;
import com.ccasro.hub.shared.domain.security.UserRole;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminStatsAdapter implements AdminStatsPort {

  private final UserStatsPort userStatsPort;
  private final VenueJpaRepository venueRepository;
  private final ResourceJpaRepository resourceRepository;
  private final BookingJpaRepository bookingRepository;

  @Override
  public AdminStats fetchStats() {
    // Usuarios
    long totalUsers = userStatsPort.countTotal();
    long totalOwners = userStatsPort.countByRole(UserRole.OWNER);
    long totalPlayers = userStatsPort.countByRole(UserRole.PLAYER);

    // Venues
    long totalVenues = venueRepository.count();
    long activeVenues = venueRepository.countByStatus(VenueStatus.ACTIVE);
    long pendingVenues = venueRepository.countByStatus(VenueStatus.PENDING_REVIEW);

    // Resources
    long totalResources = resourceRepository.count();
    long activeResources = resourceRepository.countByStatus(ResourceStatus.ACTIVE);
    long pendingResources = resourceRepository.countByStatus(ResourceStatus.PENDING_REVIEW);

    // Owner requests
    long pendingOwnerRequests = userStatsPort.countPendingOwnerRequests();

    // Bookings
    long totalBookings = bookingRepository.count();

    // Revenue this month
    LocalDate now = LocalDate.now();
    LocalDate startOfMonth = now.withDayOfMonth(1);
    LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

    BigDecimal revenueThisMonth =
        bookingRepository.sumRevenueThisMonth(startOfMonth, startOfNextMonth);

    return new AdminStats(
        totalUsers,
        totalOwners,
        totalPlayers,
        totalVenues,
        activeVenues,
        pendingVenues,
        totalResources,
        activeResources,
        pendingResources,
        pendingOwnerRequests,
        revenueThisMonth,
        totalBookings);
  }
}
