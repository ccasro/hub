package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.MyBookingView;
import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.booking.domain.Booking;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingEnrichmentService {

  private final ResourceReadPort resourceReadPort;
  private final VenueReadPort venueReadPort;

  public List<MyBookingView> enrich(List<Booking> bookings) {
    if (bookings.isEmpty()) return List.of();

    var resourceIds =
        bookings.stream().map(b -> b.getResourceId().value()).collect(Collectors.toSet());

    var resourcesById = resourceReadPort.findLiteByIds(resourceIds);

    var venueIds =
        resourcesById.values().stream()
            .map(ResourceReadPort.ResourceLite::venueId)
            .collect(Collectors.toSet());

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
