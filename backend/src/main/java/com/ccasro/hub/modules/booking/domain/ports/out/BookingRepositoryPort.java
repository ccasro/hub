package com.ccasro.hub.modules.booking.domain.ports.out;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepositoryPort {
  Booking save(Booking booking);

  Optional<Booking> findById(BookingId id);

  List<Booking> findByPlayerId(UserId playerId);

  List<Booking> findByResourceId(ResourceId resourceId);

  List<Booking> findByResourceIds(Collection<UUID> resourceIds, int page, int size);

  List<Booking> findByResourceIdAndDate(ResourceId resourceId, LocalDate date);

  List<Booking> findByVenueId(UUID venueId, int page, int size);

  List<Booking> findAll(int page, int size);

  List<Booking> findExpiredHolds(Instant now);

  boolean existsConfirmedBooking(ResourceId resourceId, LocalDate date, LocalTime startTime);
}
