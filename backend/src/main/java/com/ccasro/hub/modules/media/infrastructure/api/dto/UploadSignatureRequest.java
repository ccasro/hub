package com.ccasro.hub.modules.media.infrastructure.api.dto;

import com.ccasro.hub.modules.media.domain.UploadPurpose;
import jakarta.validation.constraints.NotNull;

public record UploadSignatureRequest(
    @NotNull UploadPurpose purpose, String venueId, String resourceId) {}
