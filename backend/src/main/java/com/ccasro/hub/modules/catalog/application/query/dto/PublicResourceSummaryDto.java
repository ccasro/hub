package com.ccasro.hub.modules.catalog.application.query.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicResourceSummaryDto(
    UUID id,
    UUID venueId,
    String name,
    String description,
    BigDecimal basePriceAmount,
    String basePriceCurrency,
    String primaryImageUrl) {}
