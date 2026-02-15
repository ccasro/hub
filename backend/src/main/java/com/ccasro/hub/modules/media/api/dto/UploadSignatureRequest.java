package com.ccasro.hub.modules.media.api.dto;

import com.ccasro.hub.modules.media.domain.UploadPurpose;
import jakarta.validation.constraints.NotNull;

public record UploadSignatureRequest(
    @NotNull UploadPurpose purpose, String companyId, String venueId, String courtId) {}
