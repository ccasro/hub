package com.ccasro.hub.modules.venue.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectVenueRequest(@NotBlank String reason) {}
