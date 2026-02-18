package com.ccasro.hub.modules.catalog.application.query.dto;

import com.ccasro.hub.modules.catalog.domain.model.venue.VenueStatus;
import java.util.UUID;

public record VenueSummaryDto(
    UUID id,
    String name,
    String description,
    String primaryImagePublicId,
    String primaryImageUrl,
    VenueStatus status) {}
