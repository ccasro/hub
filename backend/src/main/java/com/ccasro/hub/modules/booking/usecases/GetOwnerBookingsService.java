package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.MyVenueBookingView;
import com.ccasro.hub.modules.booking.application.port.out.MyVenueBookingsReadPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOwnerBookingsService {

  private final MyVenueBookingsReadPort readPort;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<MyVenueBookingView> execute(int page, int size) {
    var pageable = PageRequest.of(page, size);
    return readPort.findMyVenueBookings(currentUser.getUserId().value(), pageable).getContent();
  }
}
