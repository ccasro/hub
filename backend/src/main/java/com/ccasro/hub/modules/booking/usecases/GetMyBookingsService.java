package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.MyBookingView;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyBookingsService {

  private final BookingRepositoryPort bookingRepository;
  private final BookingEnrichmentService enrichmentService;
  private final MatchRequestRepositoryPort matchRepository;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<MyBookingView> execute() {
    var userId = currentUser.getUserId();
    var bookings = bookingRepository.findByPlayerId(userId);
    var enriched = enrichmentService.enrich(bookings);

    Set<UUID> leftMatchIds = matchRepository.findMatchIdsWherePlayerLeft(userId);
    if (leftMatchIds.isEmpty()) return enriched;

    return enriched.stream()
        .map(
            v ->
                v.matchRequestId() != null && leftMatchIds.contains(v.matchRequestId())
                    ? new MyBookingView(
                        v.booking(),
                        v.resourceName(),
                        v.venueName(),
                        v.venueCity(),
                        v.matchRequestId(),
                        true)
                    : v)
        .toList();
  }
}
