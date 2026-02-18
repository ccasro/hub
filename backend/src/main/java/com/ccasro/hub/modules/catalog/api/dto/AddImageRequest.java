package com.ccasro.hub.modules.catalog.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AddImageRequest(@NotBlank String publicId, @NotBlank String url) {}
