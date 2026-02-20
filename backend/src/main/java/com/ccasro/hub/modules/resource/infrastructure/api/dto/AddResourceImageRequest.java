package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AddResourceImageRequest(@NotBlank String url, @NotBlank String publicId) {}
