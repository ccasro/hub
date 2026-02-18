package com.ccasro.hub.modules.catalog.api.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
    @NotBlank @Size(max = 150) String street,
    @NotBlank @Size(max = 100) String city,
    @NotBlank @Size(max = 20) String postalCode,
    @NotBlank @Size(max = 100) String country) {}
