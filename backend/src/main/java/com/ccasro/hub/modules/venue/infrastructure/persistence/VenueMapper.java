package com.ccasro.hub.modules.venue.infrastructure.persistence;

import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.VenueImageReconstitutionData;
import com.ccasro.hub.modules.venue.domain.VenueImageSnapshot;
import com.ccasro.hub.modules.venue.domain.valueobjects.Address;
import com.ccasro.hub.modules.venue.domain.valueobjects.Coordinates;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueName;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.Comparator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

  private static final GeometryFactory GF = new GeometryFactory();

  public Venue toDomain(VenueEntity e) {

    Coordinates coordinates = null;

    if (e.getLocation() != null) {
      coordinates = new Coordinates(e.getLocation().getY(), e.getLocation().getX());
    }

    var imagesData =
        e.getImages().stream()
            .map(
                img ->
                    new VenueImageReconstitutionData(
                        img.getId(),
                        img.getUrl(),
                        img.getPublicId(),
                        img.getDisplayOrder(),
                        img.getCreatedAt()))
            .toList();

    return Venue.reconstitute(
        VenueId.of(e.getId()),
        UserId.from(e.getOwnerId()),
        new VenueName(e.getName()),
        e.getDescription(),
        new Address(e.getStreet(), e.getCity(), e.getCountry(), e.getPostalCode()),
        coordinates,
        imagesData,
        e.getStatus(),
        e.getRejectReason(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  public VenueEntity toEntity(Venue d) {
    VenueEntity e = new VenueEntity();
    e.setId(d.getId().value());
    e.setCreatedAt(d.getCreatedAt());
    updateEntity(d, e);
    return e;
  }

  public void updateEntity(Venue d, VenueEntity e) {
    e.setOwnerId(d.getOwnerId().value());
    e.setName(d.getName().value());
    e.setDescription(d.getDescription());

    if (d.getAddress() != null) {
      e.setStreet(d.getAddress().street());
      e.setCity(d.getAddress().city());
      e.setCountry(d.getAddress().country());
      e.setPostalCode(d.getAddress().postalCode());
    } else {
      e.setStreet(null);
      e.setCity(null);
      e.setCountry(null);
      e.setPostalCode(null);
    }

    if (d.getCoordinates() != null) {
      Point point =
          GF.createPoint(
              new Coordinate(d.getCoordinates().longitude(), d.getCoordinates().latitude()));
      point.setSRID(4326);
      e.setLocation(point);
    } else {
      e.setLocation(null);
    }

    e.setStatus(d.getStatus());
    e.setRejectReason(d.getRejectReason());
    e.setUpdatedAt(d.getUpdatedAt());

    e.getImages().clear();
    d.images().stream()
        .sorted(Comparator.comparingInt(VenueImageSnapshot::displayOrder))
        .forEach(
            img -> {
              VenueImageEntity ie = new VenueImageEntity();
              ie.setId(img.id());
              ie.setVenue(e);
              ie.setUrl(img.url());
              ie.setPublicId(img.publicId());
              ie.setDisplayOrder(img.displayOrder());
              ie.setCreatedAt(img.createdAt());
              e.getImages().add(ie);
            });
  }
}
