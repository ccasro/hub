package com.ccasro.hub.modules.catalog.infrastructure.persistence.mapper;

import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueSummaryDto;
import com.ccasro.hub.modules.catalog.application.query.dto.VenueSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.media.*;
import com.ccasro.hub.modules.catalog.domain.model.venue.*;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.VenueImageJpaEntity;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.VenueJpaEntity;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.embeddable.AddressEmbeddable;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.embeddable.GeoLocationEmbeddable;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

  public Venue toDomain(VenueJpaEntity v, List<VenueImageJpaEntity> images) {
    List<Image> domainImages = images.stream().map(this::toDomainImage).toList();

    return Venue.rehydrate(
        new VenueId(v.id),
        new UserId(v.ownerUserId),
        new VenueName(v.name),
        new Description(v.description),
        toDomainAddress(v.address),
        toDomainGeo(v.geo),
        domainImages,
        VenueStatus.valueOf(v.status),
        v.createdAt,
        v.updatedAt);
  }

  private Image toDomainImage(VenueImageJpaEntity e) {
    return new Image(
        new ImageId(e.id),
        new MediaPublicId(e.publicId),
        new MediaUrl(URI.create(e.url)),
        new ImagePosition(e.position),
        e.primary,
        e.createdAt);
  }

  public VenueJpaEntity toEntity(Venue v) {
    VenueJpaEntity e = new VenueJpaEntity();
    e.id = v.id().value();
    e.ownerUserId = v.ownerUserId().value();
    e.name = v.name().value();
    e.description = v.description() == null ? null : v.description().value();

    e.primaryImagePublicId = v.primaryImagePublicId();
    e.primaryImageUrl = v.primaryImageUrl();

    e.status = v.status().name();

    e.address = toEmbeddable(v.address());
    e.geo = toEmbeddable(v.location());

    e.createdAt = v.createdAt();
    e.updatedAt = v.updatedAt();
    return e;
  }

  public VenueImageJpaEntity toImageEntity(UUID venueId, Image img) {
    VenueImageJpaEntity e = new VenueImageJpaEntity();
    e.id = img.id().value();
    e.venueId = venueId;
    e.publicId = img.publicId().value();
    e.url = img.url().toString();
    e.position = img.position();
    e.primary = img.primary();
    e.createdAt = img.createdAt();
    return e;
  }

  private Address toDomainAddress(AddressEmbeddable a) {
    if (a == null) return null;
    return new Address(a.street, a.city, a.postalCode, a.country);
  }

  private AddressEmbeddable toEmbeddable(Address a) {
    if (a == null) return null;
    AddressEmbeddable e = new AddressEmbeddable();
    e.street = a.street();
    e.city = a.city();
    e.postalCode = a.postalCode();
    e.country = a.country();
    return e;
  }

  private GeoLocation toDomainGeo(GeoLocationEmbeddable g) {
    if (g == null) return null;
    return new GeoLocation(g.latitude, g.longitude);
  }

  private GeoLocationEmbeddable toEmbeddable(GeoLocation g) {
    if (g == null) return null;
    GeoLocationEmbeddable e = new GeoLocationEmbeddable();
    e.latitude = g.latitude();
    e.longitude = g.longitude();
    return e;
  }

  public VenueSummaryDto toSummaryDto(VenueJpaEntity v) {
    return new VenueSummaryDto(
        v.id,
        v.name,
        v.description,
        v.primaryImagePublicId,
        v.primaryImageUrl,
        VenueStatus.valueOf(v.status));
  }

  public PublicVenueSummaryDto toPublicSummaryDto(VenueJpaEntity v) {
    return new PublicVenueSummaryDto(
        v.id,
        v.name,
        v.description,
        v.primaryImageUrl,
        v.address == null ? null : v.address.street,
        v.address == null ? null : v.address.city,
        v.address == null ? null : v.address.postalCode,
        v.address == null ? null : v.address.country,
        v.geo == null ? null : v.geo.latitude,
        v.geo == null ? null : v.geo.longitude);
  }
}
