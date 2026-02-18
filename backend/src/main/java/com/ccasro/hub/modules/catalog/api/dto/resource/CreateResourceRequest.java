package com.ccasro.hub.modules.catalog.api.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateResourceRequest(
    @NotBlank @Size(max = 60) String name,
    @Size(max = 2000) String description,
    @NotBlank @Pattern(regexp = "^\\d+(\\.\\d{1,4})?$", message = "Invalid amount format")
        String basePriceAmount,
    @NotBlank @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be ISO-4217 (e.g. EUR)")
        String basePriceCurrency) {}
