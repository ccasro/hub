package com.ccasro.hub.modules.catalog.infrastructure.persistence.mapper;

import com.ccasro.hub.modules.catalog.application.query.dto.AddressDto;
import com.ccasro.hub.modules.catalog.application.query.dto.GeoLocationDto;
import com.ccasro.hub.modules.catalog.application.query.dto.ImageDto;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueDetailDto;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.VenueImageJpaEntity;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.VenueJpaEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class VenueQueryMapper {

  public PublicVenueDetailDto toPublicDetailDto(
      VenueJpaEntity v, List<VenueImageJpaEntity> images) {

    var imageDtos =
        images.stream()
            .map(img -> new ImageDto(img.id.toString(), img.url, img.primary, img.position))
            .toList();

    AddressDto addressDto = null;
    if (v.address != null) {
      addressDto =
          new AddressDto(v.address.street, v.address.city, v.address.postalCode, v.address.country);
    }

    GeoLocationDto geoDto = null;
    if (v.geo != null) {
      geoDto = new GeoLocationDto(v.geo.latitude, v.geo.longitude);
    }

    return new PublicVenueDetailDto(
        v.id.toString(), v.name, v.description, v.primaryImageUrl, addressDto, geoDto, imageDtos);
  }
}
