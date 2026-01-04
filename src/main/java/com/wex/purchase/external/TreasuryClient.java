package com.wex.purchase.external;

import com.wex.purchase.external.dto.TreasuryResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TreasuryClient {
  private static final Logger log = LogManager.getLogger(TreasuryClient.class);

  public record RateItem(LocalDate effectiveDate, BigDecimal rate) {}

  private final WebClient treasuryWebClient;

  public TreasuryClient(WebClient treasuryWebClient) {
    this.treasuryWebClient = treasuryWebClient;
  }

  public Mono<List<RateItem>> queryRates(String currencyDesc, LocalDate start, LocalDate end) {
    String uri = UriComponentsBuilder
        .fromPath("/services/api/fiscal_service/v1/accounting/od/rates_of_exchange")
        .queryParam("fields", "country_currency_desc,effective_date,exchange_rate")
        .queryParam("filter", "country_currency_desc:eq:" + currencyDesc + ",effective_date:gte:" + start + ",effective_date:lte:" + end)
        .queryParam("sort", "-effective_date")
        .queryParam("page[size]", "500")
        .build()
        .toUriString();

    log.info("Treasury API request currencyDesc={} start={} end={}", currencyDesc, start, end);

    return treasuryWebClient.get()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(TreasuryResponse.class)
        .map(resp -> (resp == null || resp.data() == null) ? List.of()
            : resp.data().stream().map(r -> new RateItem(r.effectiveDate(), r.exchangeRate())).toList());
  }
}
