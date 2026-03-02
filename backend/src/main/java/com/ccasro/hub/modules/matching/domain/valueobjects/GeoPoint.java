package com.ccasro.hub.modules.matching.domain.valueobjects;

public record GeoPoint(double latitude, double longitude) {

  public double distanceKm(GeoPoint other) {
    final double R = 6371.0;
    double dLat = Math.toRadians(other.latitude - this.latitude);
    double dLon = Math.toRadians(other.longitude - this.longitude);
    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(this.latitude))
                * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }

  public boolean isWithinRadiusKm(GeoPoint center, double radiusKm) {
    return distanceKm(center) <= radiusKm;
  }
}
