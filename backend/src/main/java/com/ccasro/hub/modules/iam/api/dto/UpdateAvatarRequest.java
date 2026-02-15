package com.ccasro.hub.modules.iam.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAvatarRequest(@NotBlank String publicId, String url) {}
