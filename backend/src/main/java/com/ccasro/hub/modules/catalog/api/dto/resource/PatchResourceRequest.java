package com.ccasro.hub.modules.catalog.api.dto.resource;

import com.ccasro.hub.modules.catalog.application.command.PatchResourceDetailsCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatchResourceRequest(
    @Size(max = 60) String name,
    @Size(max = 2000) String description,
    @Pattern(regexp = "^\\d+(\\.\\d{1,4})?$", message = "Invalid amount format (max 4 decimals)")
        String basePriceAmount,
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be ISO-4217 (e.g. EUR)")
        String basePriceCurrency) {

  @AssertTrue(message = "basePriceAmount and basePriceCurrency must be provided together")
  public boolean isPricePairValid() {
    boolean hasAmount = basePriceAmount != null && !basePriceAmount.isBlank();
    boolean hasCurrency = basePriceCurrency != null && !basePriceCurrency.isBlank();
    return hasAmount == hasCurrency;
  }

  public PatchResourceDetailsCommand toCommand() {
    return new PatchResourceDetailsCommand(name, description, basePriceAmount, basePriceCurrency);
  }
}
