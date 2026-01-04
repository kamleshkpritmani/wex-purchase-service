package com.wex.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseResponse(
    String id,
    String description,
    LocalDate transactionDate,
    BigDecimal purchaseAmountUsd
) {}
