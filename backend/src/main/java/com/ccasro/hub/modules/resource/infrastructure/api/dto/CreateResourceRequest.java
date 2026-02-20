package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResourceRequest(
    @NotBlank String name,
    String description,
    @NotNull ResourceType type,
    @NotNull Integer slotDurationMinutes) {}
