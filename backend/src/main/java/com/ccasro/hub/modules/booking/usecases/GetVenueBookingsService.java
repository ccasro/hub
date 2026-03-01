package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.MyVenueBookingView;
import com.ccasro.hub.modules.booking.application.port.out.MyVenueBookingsReadPort;
import com.ccasro.hub.modules.venue.application.ports.in.VenueAccessPolicy;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetVenueBookingsService {

  private final MyVenueBookingsReadPort readPort;
  private final VenueAccessPolicy venueAccess;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<MyVenueBookingView> execute(UUID venueId, int page, int size) {

    venueAccess.assertOwner(venueId, currentUser.getUserId());

    var pageable = PageRequest.of(page, size);

    return readPort.findByVenueId(venueId, pageable).getContent();
  }
}
