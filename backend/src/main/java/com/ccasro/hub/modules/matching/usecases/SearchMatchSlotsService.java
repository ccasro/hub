package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.matching.application.dto.MatchSlotResult;
import com.ccasro.hub.modules.matching.application.dto.SearchMatchSlotsQuery;
import com.ccasro.hub.modules.matching.domain.ports.out.EligiblePlayerPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.modules.resource.domain.ports.out.SlotAvailabilityPort;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchMatchSlotsService {

  private final VenueReadPort venueReadPort;
  private final ResourceReadPort resourceReadPort;
  private final SlotAvailabilityPort slotAvailabilityPort;
  private final EligiblePlayerPort eligiblePlayerPort;

  @Transactional(readOnly = true)
  public List<MatchSlotResult> execute(SearchMatchSlotsQuery query) {

    var venues =
        venueReadPort.findActiveVenuesNear(
            query.center().latitude(), query.center().longitude(), query.radiusKm());
    if (venues.isEmpty()) return List.of();

    var venueIds = venues.stream().map(VenueReadPort.VenueLite::id).collect(Collectors.toList());

    var resources = resourceReadPort.findActiveByVenueIds(venueIds);
    if (resources.isEmpty()) return List.of();

    Map<UUID, VenueReadPort.VenueLite> venueById =
        venues.stream().collect(Collectors.toMap(VenueReadPort.VenueLite::id, v -> v));

    int eligibleCount =
        eligiblePlayerPort
            .findEligiblePlayers(query.center(), query.radiusKm(), query.skillLevel(), null)
            .size();

    List<MatchSlotResult> results = new ArrayList<>();

    for (var resource : resources) {
      var venue = venueById.get(resource.venueId());
      if (venue == null) continue;

      GeoPoint venuePoint = new GeoPoint(venue.latitude(), venue.longitude());
      double distanceKm = Math.round(query.center().distanceKm(venuePoint) * 10.0) / 10.0;

      var slots = slotAvailabilityPort.findAvailableSlots(resource.id(), query.date(), 0);

      slots.stream()
          .filter(
              slot ->
                  !slot.startTime().isBefore(query.startTimeFrom())
                      && !slot.startTime().isAfter(query.startTimeTo()))
          // Si el usuario pidió duración concreta, filtramos por ella
          .filter(
              slot ->
                  query.slotDurationMinutes() <= 0
                      || java.time.Duration.between(slot.startTime(), slot.endTime()).toMinutes()
                          == query.slotDurationMinutes())
          .map(
              slot ->
                  new MatchSlotResult(
                      resource.id(),
                      resource.name(),
                      resource.type(),
                      venue.id(),
                      venue.name(),
                      venue.city(),
                      venue.latitude(),
                      venue.longitude(),
                      distanceKm,
                      slot.startTime(),
                      slot.endTime(),
                      slot.price(),
                      slot.currency(),
                      eligibleCount))
          .forEach(results::add);
    }

    results.sort(
        Comparator.comparingDouble(MatchSlotResult::distanceKm)
            .thenComparing(MatchSlotResult::startTime));

    return results;
  }
}
