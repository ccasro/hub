package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.venue.application.ports.in.VenueAccessPolicy;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetVenueBookingsService {

  private final BookingRepositoryPort bookingRepository;
  private final VenueAccessPolicy venueAccess;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<Booking> execute(UUID venueId, int page, int size) {

    venueAccess.assertOwner(venueId, currentUser.getUserId());

    return bookingRepository.findByVenueId(venueId, page, size);
  }
}
