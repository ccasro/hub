package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.MyBookingView;
import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyBookingsService {

  private final BookingRepositoryPort bookingRepository;
  private final ResourceReadPort resourceReadPort;
  private final VenueReadPort venueReadPort;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<MyBookingView> execute() {
    var bookings = bookingRepository.findByPlayerId(currentUser.getUserId());

    var resourceIds =
        bookings.stream()
            .map(b -> b.getResourceId().value())
            .collect(java.util.stream.Collectors.toSet());

    var resourcesById = resourceReadPort.findLiteByIds(resourceIds);

    var venueIds =
        resourcesById.values().stream()
            .map(ResourceReadPort.ResourceLite::venueId)
            .collect(java.util.stream.Collectors.toSet());

    var venuesById = venueReadPort.findLiteByIds(venueIds);

    return bookings.stream()
        .map(
            b -> {
              var r = resourcesById.get(b.getResourceId().value());
              var v = (r != null) ? venuesById.get(r.venueId()) : null;

              return new MyBookingView(
                  b,
                  r != null ? r.name() : null,
                  v != null ? v.name() : null,
                  v != null ? v.city() : null);
            })
        .toList();
  }
}
