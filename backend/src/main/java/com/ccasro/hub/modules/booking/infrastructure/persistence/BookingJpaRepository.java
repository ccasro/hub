package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {

  List<BookingEntity> findByPlayerId(UUID playerId);

  List<BookingEntity> findByResourceId(UUID resourceId);

  List<BookingEntity> findByResourceIdAndBookingDate(UUID resourceId, LocalDate date);

  @Query(
      """
        SELECT COUNT(b) > 0 FROM BookingEntity b
        WHERE b.resourceId = :resourceId
        AND b.bookingDate = :date
        AND b.startTime = :startTime
        AND b.status != 'CANCELLED'
        """)
  boolean existsConfirmedBooking(
      @Param("resourceId") UUID resourceId,
      @Param("date") LocalDate date,
      @Param("startTime") LocalTime startTime);

  @Query(
      """
        SELECT b FROM BookingEntity b
        WHERE b.resourceId IN (
            SELECT r.id FROM ResourceEntity r
            WHERE r.venueId = :venueId
        )
        ORDER BY b.bookingDate DESC, b.startTime ASC
        """)
  Page<BookingEntity> findByVenueId(@Param("venueId") UUID venueId, Pageable pageable);

  List<BookingEntity> findByResourceIdAndBookingDateAndStatusNot(
      UUID resourceId, LocalDate date, BookingStatus status);

  @Query(
      """
  SELECT b FROM BookingEntity b
  WHERE b.status = 'PENDING_PAYMENT'
    AND b.expiresAt IS NOT NULL
    AND b.expiresAt < :now
  ORDER BY b.expiresAt ASC
""")
  List<BookingEntity> findExpiredHolds(@Param("now") Instant now);

  long countByStatus(BookingStatus status);

  @Query(
      """
    SELECT COALESCE(SUM(b.pricePaid), 0) FROM BookingEntity b
    WHERE b.paymentStatus = 'PAID'
    AND b.bookingDate >= :startOfMonth
    AND b.bookingDate < :startOfNextMonth
    """)
  BigDecimal sumRevenueThisMonth(
      @Param("startOfMonth") LocalDate startOfMonth,
      @Param("startOfNextMonth") LocalDate startOfNextMonth);

  @Query(
      """
    SELECT b FROM BookingEntity b
    WHERE b.resourceId IN :resourceIds
    ORDER BY b.bookingDate DESC, b.startTime ASC
""")
  Page<BookingEntity> findByResourceIds(
      @Param("resourceIds") Collection<UUID> resourceIds, Pageable pageable);
}
