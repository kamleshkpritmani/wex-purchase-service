package com.wex.purchase.util;

import com.wex.purchase.model.Purchase;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CurrencyConversionPolicy {
  private CurrencyConversionPolicy(){}

  public static BigDecimal convert(Purchase purchase, BigDecimal rate) {
    return purchase.getPurchaseAmountUsd()
        .multiply(rate)
        .setScale(2, RoundingMode.HALF_UP);
  }
}
