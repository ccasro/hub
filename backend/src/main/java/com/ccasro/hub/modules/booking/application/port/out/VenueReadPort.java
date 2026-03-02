package com.ccasro.hub.modules.booking.application.port.out;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface VenueReadPort {
  Map<UUID, VenueLite> findLiteByIds(Set<UUID> ids);

  record VenueLite(UUID id, String name, String city, Double latitude, Double longitude) {
    public VenueLite(UUID id, String name, String city) {
      this(id, name, city, null, null);
    }
  }

  List<VenueLite> findActiveVenuesNear(double lat, double lng, double radiusKm);
}
