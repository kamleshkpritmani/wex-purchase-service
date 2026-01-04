package com.wex.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePurchaseRequest(
    @NotBlank @Size(max = 50) String description,
    @NotNull LocalDate transactionDate,
    @NotNull @DecimalMin("0.01") BigDecimal purchaseAmountUsd
) {}
