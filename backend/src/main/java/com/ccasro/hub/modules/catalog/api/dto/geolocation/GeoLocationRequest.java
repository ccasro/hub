package com.ccasro.hub.modules.catalog.api.dto.geolocation;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GeoLocationRequest(
    @NotNull @DecimalMin(value = "-90") @DecimalMax(value = "90") BigDecimal latitude,
    @NotNull @DecimalMin(value = "-180") @DecimalMax(value = "180") BigDecimal longitude) {}
