package com.ccasro.hub.modules.venue.application.dto;

import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;

public record UpdateVenueCommand(
    VenueId venueId,
    String name,
    String description,
    String street,
    String city,
    String country,
    String postalCode,
    double latitude,
    double longitude) {}
