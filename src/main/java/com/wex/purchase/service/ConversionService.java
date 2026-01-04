package com.wex.purchase.service;

import com.wex.purchase.dto.ConvertedPurchaseResponse;
import com.wex.purchase.external.TreasuryClient;
import com.wex.purchase.model.Purchase;
import com.wex.purchase.util.CurrencyConversionPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;

@Service
public class ConversionService {
  private static final Logger log = LogManager.getLogger(ConversionService.class);
  private final TreasuryClient client;

  public ConversionService(TreasuryClient client) {
    this.client = client;
  }

  /**
   * Requirement:
   * - Use a conversion rate with the condition   effective_date <= purchaseDate within last 6 months.
   * - If none exists, return error: "purchase cannot be converted to the target currency"
   * - Round converted amount to 2 decimals.
   */
  @Cacheable(cacheNames = "rates", key = "#currency + '_' + (#p?.transactionDate ?: 'null')")
  public ConvertedPurchaseResponse convert(Purchase p, String currency) {
    LocalDate purchaseDate = p.getTransactionDate();
    LocalDate start = purchaseDate.minusMonths(6);

    var items = client.queryRates(currency, start, purchaseDate).block();
    if (items == null || items.isEmpty()) {
      log.warn("No rates returned currency={} start={} end={} purchaseId={}", currency, start, purchaseDate, p.getId());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "purchase cannot be converted to the target currency");
    }

    var latest = items.stream()
        .filter(r -> !r.effectiveDate().isBefore(start) && !r.effectiveDate().isAfter(purchaseDate))
        .max(Comparator.comparing(TreasuryClient.RateItem::effectiveDate))
        .orElse(null);

    if (latest == null) {
      log.warn("No suitable rate within 6-month window currency={} purchaseDate={} purchaseId={}", currency, purchaseDate, p.getId());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "purchase cannot be converted to the target currency");
    }

    var converted = CurrencyConversionPolicy.convert(p, latest.rate());
    log.info("Converted purchase id={} currency={} rateDate={} rate={} converted={}", p.getId(), currency, latest.effectiveDate(), latest.rate(), converted);

    return new ConvertedPurchaseResponse(
        p.getId(),
        p.getDescription(),
        p.getTransactionDate(),
        currency,
        p.getPurchaseAmountUsd(),
        latest.effectiveDate(),
        latest.rate(),
        converted
    );
  }
}
