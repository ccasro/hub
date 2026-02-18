package com.ccasro.hub.modules.iam.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAvatarRequest(@NotBlank String publicId, String url) {}
