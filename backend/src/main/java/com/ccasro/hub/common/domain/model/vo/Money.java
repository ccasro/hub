package com.ccasro.hub.common.domain.model.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

  public Money {
    Objects.requireNonNull(amount, "amount is required");
    Objects.requireNonNull(currency, "currency is required");

    if (amount.signum() < 0) throw new IllegalArgumentException("amount must be >= 0");

    int scale = currency.getDefaultFractionDigits();
    if (scale < 0) scale = 2;

    try {
      amount = amount.setScale(scale, RoundingMode.UNNECESSARY);
    } catch (ArithmeticException ex) {
      throw new IllegalArgumentException(
          "max " + scale + " decimals for " + currency.getCurrencyCode(), ex);
    }
  }

  public static Money of(String rawAmount, String currencyCode) {
    Objects.requireNonNull(rawAmount, "amount is required");
    Objects.requireNonNull(currencyCode, "currency is required");

    return new Money(
        new BigDecimal(rawAmount.trim()), Currency.getInstance(currencyCode.trim().toUpperCase()));
  }

  public static Money eur(String rawAmount) {
    return of(rawAmount, "EUR");
  }
}
