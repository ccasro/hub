package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.booking.application.port.out.ResourceReadPort;
import com.ccasro.hub.modules.booking.application.port.out.VenueReadPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckInService {

  private static final double CHECK_IN_RADIUS_KM = 0.2;
  private static final int CHECK_IN_WINDOW_BEFORE_MINUTES = 30;
  private static final int CHECK_IN_WINDOW_AFTER_MINUTES = 30;
  private static final double MAX_GPS_ACCURACY_METERS = 100.0;

  private final MatchRequestRepositoryPort matchRepository;
  private final ResourceReadPort resourceReadPort;
  private final VenueReadPort venueReadPort;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public void execute(UUID matchId, double playerLat, double playerLng, double accuracyMeters) {
    if (accuracyMeters > MAX_GPS_ACCURACY_METERS) {
      throw new IllegalStateException(
          "GPS accuracy is too low ("
              + (int) accuracyMeters
              + "m). Move to an open area and try again");
    }

    UserId playerId = currentUser.getUserId();

    MatchRequest match =
        matchRepository
            .findById(new MatchRequestId(matchId))
            .orElseThrow(() -> new MatchNotFoundException("Match not found"));

    validateTimeWindow(match);
    validateLocation(match, playerLat, playerLng);

    match.checkIn(playerId, clock);
    matchRepository.save(match);

    log.info("Player {} checked in for match {}", playerId.value(), matchId);
  }

  private void validateTimeWindow(MatchRequest match) {
    LocalDateTime matchStart = LocalDateTime.of(match.getBookingDate(), match.getStartTime());
    LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    LocalDateTime windowStart = matchStart.minusMinutes(CHECK_IN_WINDOW_BEFORE_MINUTES);
    LocalDateTime windowEnd = matchStart.plusMinutes(CHECK_IN_WINDOW_AFTER_MINUTES);

    if (now.isBefore(windowStart) || now.isAfter(windowEnd)) {
      throw new IllegalStateException(
          "Check-in is only available from "
              + CHECK_IN_WINDOW_BEFORE_MINUTES
              + " minutes before to "
              + CHECK_IN_WINDOW_AFTER_MINUTES
              + " minutes after the match starts");
    }
  }

  private void validateLocation(MatchRequest match, double playerLat, double playerLng) {
    var resourceLite =
        resourceReadPort
            .findLiteByIds(List.of(match.getResourceId().value()))
            .get(match.getResourceId().value());

    if (resourceLite == null) {
      log.warn("Resource not found for match {}, skipping location check", match.getId().value());
      return;
    }

    var venueLite =
        venueReadPort.findLiteByIds(Set.of(resourceLite.venueId())).get(resourceLite.venueId());

    if (venueLite == null || venueLite.latitude() == null || venueLite.longitude() == null) {
      log.warn(
          "Venue coordinates not found for match {}, skipping location check",
          match.getId().value());
      return;
    }

    GeoPoint venueLocation = new GeoPoint(venueLite.latitude(), venueLite.longitude());
    GeoPoint playerLocation = new GeoPoint(playerLat, playerLng);
    double distanceKm = playerLocation.distanceKm(venueLocation);

    if (distanceKm > CHECK_IN_RADIUS_KM) {
      throw new IllegalStateException(
          "You must be within "
              + (int) (CHECK_IN_RADIUS_KM * 1000)
              + " meters of the venue to check in");
    }
  }
}
