package com.ccasro.hub.modules.venue.application.dto;

public record CreateVenueCommand(
    String name,
    String description,
    String street,
    String city,
    String country,
    String postalCode,
    double latitude,
    double longitude) {}
