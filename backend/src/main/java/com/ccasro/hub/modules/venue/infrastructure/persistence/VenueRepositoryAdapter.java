package com.ccasro.hub.modules.venue.infrastructure.persistence;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.ports.out.VenueRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VenueRepositoryAdapter implements VenueRepositoryPort {

  private final VenueJpaRepository jpa;
  private final VenueMapper mapper;

  @Override
  public Venue save(Venue venue) {
    VenueEntity saved;

    if (venue.getId() != null && jpa.existsById(venue.getId().value())) {
      VenueEntity managed = jpa.findById(venue.getId().value()).orElseThrow();
      mapper.updateEntity(venue, managed);
      saved = jpa.save(managed);
    } else {
      saved = jpa.save(mapper.toEntity(venue));
    }
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Venue> findById(VenueId id) {
    return jpa.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public List<Venue> findAllActive() {
    return jpa.findByStatus(VenueStatus.ACTIVE).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Venue> findByOwnerId(UserId ownerId) {
    return jpa.findByOwnerIdAndStatusNot(ownerId.value(), VenueStatus.REJECTED).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Venue> findAll(int page, int size) {
    return jpa.findAll(PageRequest.of(page, size)).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Venue> findByStatus(VenueStatus status) {
    return jpa.findByStatus(status).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Venue> findActiveNearby(double lat, double lng, double radiusMeters) {
    return jpa.findActiveNearby(lat, lng, radiusMeters).stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(VenueId id) {
    jpa.deleteById(id.value());
  }
}
