package com.wex.purchase.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TreasuryResponse(List<Row> data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static record Row(
      @JsonProperty("country_currency_desc") String countryCurrencyDesc,
      @JsonProperty("effective_date") LocalDate effectiveDate,
      @JsonProperty("exchange_rate") BigDecimal exchangeRate
  ) {}
}
