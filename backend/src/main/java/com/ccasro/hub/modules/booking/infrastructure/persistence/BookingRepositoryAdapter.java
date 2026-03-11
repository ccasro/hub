package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryAdapter implements BookingRepositoryPort {

  private final BookingJpaRepository jpa;
  private final BookingMapper mapper;

  @Override
  public Booking save(Booking booking) {
    BookingEntity entity = jpa.findById(booking.getId().value()).orElseGet(BookingEntity::new);
    mapper.fill(entity, booking);
    return mapper.toDomain(jpa.save(entity));
  }

  @Override
  public Optional<Booking> findById(BookingId id) {
    return jpa.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public List<Booking> findByPlayerId(UserId playerId) {
    return jpa.findByPlayerId(playerId.value()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Booking> findByResourceId(ResourceId resourceId) {
    return jpa.findByResourceId(resourceId.value()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Booking> findByResourceIds(Collection<UUID> resourceIds, int page, int size) {
    return jpa.findByResourceIds(resourceIds, PageRequest.of(page, size)).getContent().stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Booking> findByResourceIdAndDate(ResourceId resourceId, LocalDate date) {
    return jpa.findByResourceIdAndBookingDate(resourceId.value(), date).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Booking> findByVenueId(UUID venueId, int page, int size) {
    return jpa.findByVenueId(venueId, PageRequest.of(page, size)).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Booking> findAll(int page, int size) {
    return jpa.findAll(PageRequest.of(page, size)).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Booking> findExpiredHolds(Instant now) {
    return jpa.findExpiredHolds(now).stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsActiveBooking(ResourceId resourceId, LocalDate date, LocalTime startTime) {
    return jpa.existsActiveBooking(resourceId.value(), date, startTime);
  }
}
