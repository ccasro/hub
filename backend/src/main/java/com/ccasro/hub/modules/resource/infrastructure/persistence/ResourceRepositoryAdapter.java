package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ResourceRepositoryAdapter implements ResourceRepositoryPort {

  private final ResourceJpaRepository jpa;
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
}
