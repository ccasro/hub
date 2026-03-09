package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.MyBookingView;
import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingEnrichmentService {

  private final ResourceReadPort resourceReadPort;
  private final VenueReadPort venueReadPort;
  private final MatchRequestRepositoryPort matchRequestRepository;

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

    // Single batch query for active matches — keyed by resourceId:startTime
    Map<String, UUID> slotToMatchId = buildSlotToMatchIdMap(bookings, resourceIds);

    return bookings.stream()
        .map(
            b -> {
              var r = resourcesById.get(b.getResourceId().value());
              var v = (r != null) ? venuesById.get(r.venueId()) : null;
              UUID matchRequestId =
                  slotToMatchId.get(slotKey(b.getResourceId().value(), b.getSlot().startTime()));
              return new MyBookingView(
                  b,
                  r != null ? r.name() : null,
                  v != null ? v.name() : null,
                  v != null ? v.city() : null,
                  matchRequestId,
                  false);
            })
        .toList();
  }

  /**
   * Fetches all active matches for the given resource IDs in one query, then builds a lookup map
   * keyed by "resourceId:startTime" so the per-booking resolution is O(1).
   *
   * <p>Only bookings in PENDING_MATCH or CONFIRMED status can be associated with a match; others
   * are left with a null matchRequestId.
   */
  private Map<String, UUID> buildSlotToMatchIdMap(List<Booking> bookings, Set<UUID> resourceIds) {
    boolean anyMatchCandidate =
        bookings.stream()
            .anyMatch(
                b ->
                    b.getStatus() == BookingStatus.PENDING_MATCH
                        || b.getStatus() == BookingStatus.CONFIRMED);

    if (!anyMatchCandidate) return Map.of();

    return matchRequestRepository.findActiveByResourceIds(resourceIds).stream()
        .collect(
            Collectors.toMap(
                m -> slotKey(m.getResourceId().value(), m.getStartTime()),
                m -> m.getId().value(),
                // keep first if two matches somehow share the same slot (shouldn't happen)
                (existing, duplicate) -> existing));
  }

  private static String slotKey(UUID resourceId, LocalTime startTime) {
    return resourceId + ":" + startTime;
  }
}
