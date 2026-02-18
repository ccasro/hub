package com.ccasro.hub.modules.catalog.infrastructure.persistence;

import com.ccasro.hub.modules.catalog.application.query.PublicResourceQueryPort;
import com.ccasro.hub.modules.catalog.application.query.ResourceSummaryQueryPort;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicResourceSummaryDto;
import com.ccasro.hub.modules.catalog.application.query.dto.ResourceSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.ResourceImageSpringDataRepository;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.ResourceSpringDataRepository;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.mapper.ResourceMapper;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResourceJpaAdapter
    implements ResourceRepositoryPort, ResourceSummaryQueryPort, PublicResourceQueryPort {

  private final ResourceSpringDataRepository resources;
  private final ResourceImageSpringDataRepository images;
  private final ResourceMapper mapper;

  // ========= ResourceRepositoryPort =========

  @Override
  @Transactional
  public void save(Resource resource) {
    UUID id = resource.id().value();

    resources.save(mapper.toEntity(resource));

    images.deleteByResourceId(id);
    var imageEntities =
        resource.images().stream().map(img -> mapper.toImageEntity(id, img)).toList();
    images.saveAll(imageEntities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Resource> findById(ResourceId id) {
    return resources
        .findById(id.value())
        .map(r -> mapper.toDomain(r, images.findByResourceIdOrderByPositionAsc(id.value())));
  }

  @Override
  @Transactional(readOnly = true)
  public int countByVenueId(VenueId venueId) {
    long count = resources.countByVenueId(venueId.value());
    return Math.toIntExact(count);
  }

  // ========= ResourceSummaryQueryPort (mine / summaries) =========

  @Override
  @Transactional(readOnly = true)
  public List<ResourceSummaryDto> findSummariesByOwnerUserId(UserId ownerId) {
    return resources.findByOwnerUserId(ownerId.value()).stream().map(mapper::toSummaryDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResourceSummaryDto> findSummariesByOwnerUserIdAndVenueId(
      UserId ownerId, VenueId venueId) {
    return resources.findByOwnerUserIdAndVenueId(ownerId.value(), venueId.value()).stream()
        .map(mapper::toSummaryDto)
        .toList();
  }

  // ========= PublicResourceQueryPort (public / summaries) =========
  // Asumimos que PUBLIC = ACTIVE
  // Si tu campo status es String, cambia ResourceStatus.ACTIVE por "ACTIVE"

  @Override
  @Transactional(readOnly = true)
  public List<PublicResourceSummaryDto> findAllPublic() {
    return resources.findAllPublic().stream().map(mapper::toPublicSummaryDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<PublicResourceSummaryDto> findPublicSummariesByVenueId(VenueId venueId) {
    return resources.findPublicByVenueId(venueId.value()).stream()
        .map(mapper::toPublicSummaryDto)
        .toList();
  }
}
