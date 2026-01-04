package com.wex.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConvertedPurchaseResponse(
    String id,
    String description,
    LocalDate transactionDate,
    String targetCurrency,
    BigDecimal originalAmountUsd,
    LocalDate exchangeRateDate,
    BigDecimal exchangeRateUsed,
    BigDecimal convertedAmount
) {}
