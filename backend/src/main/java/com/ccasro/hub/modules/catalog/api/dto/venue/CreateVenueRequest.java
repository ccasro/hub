package com.ccasro.hub.modules.catalog.api.dto.venue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVenueRequest(
    @NotBlank @Size(max = 80) String name, @Size(max = 2000) String description) {}
