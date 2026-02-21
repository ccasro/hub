package com.ccasro.hub.modules.booking.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FakePaymentRequest(@NotNull BigDecimal amount, @NotBlank String currency) {}
