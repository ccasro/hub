package com.ccasro.hub.modules.catalog.infrastructure.persistence;

import com.ccasro.hub.modules.catalog.application.query.PublicVenueQueryPort;
import com.ccasro.hub.modules.catalog.application.query.VenueSummaryQueryPort;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueDetailDto;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueSummaryDto;
import com.ccasro.hub.modules.catalog.application.query.dto.VenueSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueStatus;
import com.ccasro.hub.modules.catalog.domain.port.VenueRepositoryPort;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.VenueImageSpringDataRepository;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.VenueSpringDataRepository;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.mapper.VenueMapper;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.mapper.VenueQueryMapper;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VenueJpaAdapter
    implements VenueRepositoryPort, VenueSummaryQueryPort, PublicVenueQueryPort {

  private final VenueSpringDataRepository venues;
  private final VenueImageSpringDataRepository images;
  private final VenueMapper mapper;
  private final VenueQueryMapper queryMapper;

  // ===== VenueRepositoryPort =====

  @Override
  @Transactional
  public void save(Venue venue) {
    UUID id = venue.id().value();

    venues.save(mapper.toEntity(venue));

    images.deleteByVenueId(id);
    var imageEntities = venue.images().stream().map(img -> mapper.toImageEntity(id, img)).toList();
    images.saveAll(imageEntities);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Venue> findById(VenueId id) {
    return venues
        .findById(id.value())
        .map(v -> mapper.toDomain(v, images.findByVenueIdOrderByPositionAsc(id.value())));
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByIdAndOwnerUserId(VenueId venueId, UserId ownerUserId) {
    return venues.existsByIdAndOwnerUserId(venueId.value(), ownerUserId.value());
  }

  @Override
  @Transactional(readOnly = true)
  public int countByOwnerUserId(UserId ownerUserId) {
    return Math.toIntExact(venues.countByOwnerUserId(ownerUserId.value()));
  }

  // ===== VenueSummaryQueryPort (mine) =====

  @Override
  @Transactional(readOnly = true)
  public List<VenueSummaryDto> findSummariesByOwnerUserId(UserId ownerUserId) {
    return venues.findMine(ownerUserId.value()).stream().map(mapper::toSummaryDto).toList();
  }

  // ===== PublicVenueQueryPort (public) =====

  @Override
  @Transactional(readOnly = true)
  public List<PublicVenueSummaryDto> findAllPublicSummaries() {
    return venues.findAllPublic().stream().map(mapper::toPublicSummaryDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PublicVenueDetailDto> findPublicDetailById(VenueId id) {

    return venues
        .findByIdAndStatus(id.value(), VenueStatus.ACTIVE)
        .map(
            v -> {
              var imgs = images.findByVenueIdOrderByPositionAsc(id.value());
              return queryMapper.toPublicDetailDto(v, imgs);
            });
  }
}
