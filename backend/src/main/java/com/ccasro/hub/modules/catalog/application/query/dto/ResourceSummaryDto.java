package com.ccasro.hub.modules.catalog.application.query.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ResourceSummaryDto(
    UUID id,
    UUID venueId,
    String name,
    String description,
    BigDecimal basePriceAmount,
    String basePriceCurrency,
    String primaryImagePublicId,
    String primaryImageUrl,
    String status) {}
