package com.ccasro.hub.modules.venue.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVenueRequest(
    @NotBlank String name,
    String description,
    @NotBlank String street,
    @NotBlank String city,
    @NotBlank String country,
    String postalCode,
    @NotNull Double latitude,
    @NotNull Double longitude) {}
