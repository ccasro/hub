package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceSchedulePort;
import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ResourceRepositoryAdapter implements ResourceRepositoryPort, ResourceSchedulePort {

  private final ResourceJpaRepository jpa;
  private final ResourceScheduleJpaRepository jpaSchedule;
  private final ResourceMapper mapper;

  @Override
  public Resource save(Resource resource) {
    ResourceEntity saved =
        jpa.findById(resource.getId().value())
            .map(
                managed -> {
                  mapper.updateEntity(resource, managed);
                  return jpa.save(managed);
                })
            .orElseGet(() -> jpa.save(mapper.toEntity(resource)));

    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Resource> findById(ResourceId id) {
    return jpa.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public List<Resource> findByVenueId(VenueId venueId) {
    return jpa.findByVenueId(venueId.value()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Resource> findByVenueIds(Collection<UUID> venueIds) {
    return jpa.findByVenueIdIn(venueIds).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Resource> findActiveByVenueId(VenueId venueId) {
    return jpa.findByVenueIdAndStatus(venueId.value(), ResourceStatus.ACTIVE).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Resource> findByStatus(ResourceStatus status) {
    return jpa.findByStatus(status).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Resource> findAll(int page, int size) {
    return jpa.findAll(PageRequest.of(page, size)).stream().map(mapper::toDomain).toList();
  }

  @Override
  public Resource upsertSchedule(
      ResourceId resourceId, DayOfWeek day, LocalTime opening, LocalTime closing) {
    ResourceEntity entity =
        jpa.findById(resourceId.value()).orElseThrow(ResourceNotFoundException::new);
    entity.getSchedules().stream()
        .filter(s -> s.getDayOfWeek() == day)
        .findFirst()
        .ifPresentOrElse(
            existing -> {
              existing.setOpeningTime(opening);
              existing.setClosingTime(closing);
            },
            () -> {
              ResourceScheduleEntity e = new ResourceScheduleEntity();
              e.setId(UUID.randomUUID());
              e.setResource(entity);
              e.setDayOfWeek(day);
              e.setOpeningTime(opening);
              e.setClosingTime(closing);
              entity.getSchedules().add(e);
            });
    return mapper.toDomain(jpa.save(entity));
  }

  @Override
  public Resource removeSchedule(ResourceId resourceId, DayOfWeek day) {
    ResourceEntity entity =
        jpa.findById(resourceId.value()).orElseThrow(ResourceNotFoundException::new);
    entity.getSchedules().removeIf(s -> s.getDayOfWeek() == day);
    return mapper.toDomain(jpa.save(entity));
  }
}
