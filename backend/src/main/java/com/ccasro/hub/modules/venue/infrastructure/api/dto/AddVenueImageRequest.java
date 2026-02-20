package com.ccasro.hub.modules.venue.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AddVenueImageRequest(@NotBlank String url, @NotBlank String publicId) {}
