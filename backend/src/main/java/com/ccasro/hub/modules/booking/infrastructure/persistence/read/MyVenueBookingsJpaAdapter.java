package com.ccasro.hub.modules.booking.infrastructure.persistence.read;

import com.ccasro.hub.modules.booking.application.dto.MyVenueBookingView;
import com.ccasro.hub.modules.booking.application.port.out.MyVenueBookingsReadPort;
import com.ccasro.hub.modules.booking.infrastructure.persistence.BookingReadJpaRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyVenueBookingsJpaAdapter implements MyVenueBookingsReadPort {

  private final BookingReadJpaRepository repo;

  @Override
  public Page<MyVenueBookingView> findMyVenueBookings(UUID ownerId, Pageable pageable) {
    return repo.findMyVenueBookings(ownerId, pageable)
        .map(
            r ->
                new MyVenueBookingView(
                    r.bookingId(),
                    r.resourceId(),
                    r.playerId(),
                    r.bookingDate(),
                    r.startTime(),
                    r.endTime(),
                    r.pricePaid(),
                    r.currency(),
                    r.status(),
                    r.paymentStatus(),
                    r.resourceName(),
                    r.venueName(),
                    r.city()));
  }
}
