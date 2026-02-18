package com.ccasro.hub.modules.catalog.infrastructure.persistence.mapper;

import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.common.domain.model.vo.Money;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicResourceSummaryDto;
import com.ccasro.hub.modules.catalog.application.query.dto.ResourceSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.media.*;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceName;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceStatus;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.ResourceImageJpaEntity;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.ResourceJpaEntity;
import java.net.URI;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

  public Resource toDomain(ResourceJpaEntity r, List<ResourceImageJpaEntity> images) {
    List<Image> domainImages = images.stream().map(this::toDomainImage).toList();

    return Resource.rehydrate(
        new ResourceId(r.id),
        new VenueId(r.venueId),
        new ResourceName(r.name),
        new Description(r.description),
        new Money(r.basePriceAmount, Currency.getInstance(r.basePriceCurrency)),
        ResourceStatus.valueOf(r.status),
        domainImages,
        r.createdAt,
        r.updatedAt);
  }

  private Image toDomainImage(ResourceImageJpaEntity e) {
    return new Image(
        new ImageId(e.id),
        new MediaPublicId(e.publicId),
        new MediaUrl(URI.create(e.url)),
        new ImagePosition(e.position),
        e.primary,
        e.createdAt);
  }

  public ResourceJpaEntity toEntity(Resource r) {
    ResourceJpaEntity e = new ResourceJpaEntity();
    e.id = r.id().value();
    e.venueId = r.venueId().value();
    e.name = r.name().value();
    e.description = r.description() == null ? null : r.description().value();

    e.basePriceAmount = r.basePricePerHour().amount();
    e.basePriceCurrency = r.basePricePerHour().currency().getCurrencyCode();

    e.primaryImagePublicId = r.primaryImagePublicId();
    e.primaryImageUrl = r.primaryImageUrl();

    e.status = r.status().name();

    e.createdAt = r.createdAt();
    e.updatedAt = r.updatedAt();
    return e;
  }

  public ResourceImageJpaEntity toImageEntity(UUID resourceId, Image img) {
    ResourceImageJpaEntity e = new ResourceImageJpaEntity();
    e.id = img.id().value();
    e.resourceId = resourceId;
    e.publicId = img.publicId().value();
    e.url = img.url().toString();
    e.position = img.position();
    e.primary = img.primary();
    e.createdAt = img.createdAt();
    return e;
  }

  public ResourceSummaryDto toSummaryDto(ResourceJpaEntity r) {
    return new ResourceSummaryDto(
        r.id,
        r.venueId,
        r.name,
        r.description,
        r.basePriceAmount,
        r.basePriceCurrency,
        r.primaryImagePublicId,
        r.primaryImageUrl,
        r.status);
  }

  public PublicResourceSummaryDto toPublicSummaryDto(ResourceJpaEntity r) {
    return new PublicResourceSummaryDto(
        r.id,
        r.venueId,
        r.name,
        r.description,
        r.basePriceAmount,
        r.basePriceCurrency,
        r.primaryImageUrl);
  }
}
