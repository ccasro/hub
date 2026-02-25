package com.ccasro.hub.modules.booking.application.port.out;

import com.ccasro.hub.modules.booking.application.dto.MyVenueBookingView;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MyVenueBookingsReadPort {
  Page<MyVenueBookingView> findMyVenueBookings(UUID ownerId, Pageable pageable);
}
