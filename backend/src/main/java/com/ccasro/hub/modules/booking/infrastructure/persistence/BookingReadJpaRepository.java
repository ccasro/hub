package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.infrastructure.persistence.projection.MyVenueBookingRow;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingReadJpaRepository extends JpaRepository<BookingEntity, UUID> {

  @Query(
      """
    select new com.ccasro.hub.modules.booking.infrastructure.persistence.projection.MyVenueBookingRow(
      b.id,
      b.resourceId,
      b.playerId,
      b.bookingDate,
      b.startTime,
      b.endTime,
      b.pricePaid,
      b.currency,
      b.status,
      b.paymentStatus,
      b.createdAt,
      r.name,
      v.id,
      v.name,
      v.city
    )
    from BookingEntity b
    join ResourceEntity r on r.id = b.resourceId
    join VenueEntity v on v.id = r.venueId
    where v.ownerId = :ownerId
    order by b.createdAt desc, b.id desc
  """)
  Page<MyVenueBookingRow> findMyVenueBookings(UUID ownerId, Pageable pageable);
}
