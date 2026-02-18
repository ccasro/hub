package com.ccasro.hub.modules.catalog.application.query.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicVenueSummaryDto(
    UUID id,
    String name,
    String description,
    String primaryImageUrl,
    String street,
    String city,
    String postalCode,
    String country,
    BigDecimal latitude,
    BigDecimal longitude) {}
