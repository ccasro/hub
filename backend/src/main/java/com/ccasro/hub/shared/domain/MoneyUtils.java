package com.ccasro.hub.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class MoneyUtils {

  private static final int SCALE = 2;

  private MoneyUtils() {}

  /**
   * Splits a monetary amount into {@code parts} shares, distributing any remainder cents to the
   * first elements. The returned list always sums exactly to {@code total}.
   *
   * <p>Example: split(10.00, 3) → [3.34, 3.33, 3.33]
   */
  public static List<BigDecimal> split(BigDecimal total, int parts) {
    if (parts <= 0) throw new IllegalArgumentException("parts must be > 0");

    long totalCents =
        total.scaleByPowerOfTen(SCALE).setScale(0, RoundingMode.HALF_UP).longValueExact();
    long base = totalCents / parts;
    long remainder = totalCents % parts;

    List<BigDecimal> shares = new ArrayList<>(parts);
    for (int i = 0; i < parts; i++) {
      long cents = base + (i < remainder ? 1 : 0);
      shares.add(BigDecimal.valueOf(cents).scaleByPowerOfTen(-SCALE));
    }
    return shares;
  }
}
