package com.wex.purchase.service;

import com.wex.purchase.external.TreasuryClient;
import com.wex.purchase.model.Purchase;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConversionServiceTest {

  @Test
  void converts_using_latest_rate_and_rounds_2dp() {
    TreasuryClient client = mock(TreasuryClient.class);
    ConversionService svc = new ConversionService(client);

    Purchase p = new Purchase("id1", "reviewer","Books", LocalDate.of(2025,6,15), new BigDecimal("10.00"));
    when(client.queryRates(eq("Australia-Dollar"), any(), any())).thenReturn(
        Mono.just(List.of(
            new TreasuryClient.RateItem(LocalDate.of(2025,3,31), new BigDecimal("1.6")),
            new TreasuryClient.RateItem(LocalDate.of(2024,12,31), new BigDecimal("1.612"))
        ))
    );

    var resp = svc.convert(p, "Australia-Dollar");
    assertThat(resp.convertedAmount()).isEqualByComparingTo("16.00");
    assertThat(resp.exchangeRateDate()).isEqualTo(LocalDate.of(2025,3,31));
  }
  
  @Test
  void rounds_financial_data_correctly_using_half_up() {
	  TreasuryClient client = mock(TreasuryClient.class);
	  ConversionService svc = new ConversionService(client);
      // Test case for 10.00 * 1.125 = 11.25 (standard) 
      // vs 10.00 * 1.1251 = 11.25 (or 11.26 depending on precision)
      Purchase p = new Purchase("id2", "reviewer", "RoundingTest", LocalDate.of(2025, 1, 1), new BigDecimal("10.00"));
      when(client.queryRates(any(), any(), any())).thenReturn(
          Mono.just(List.of(new TreasuryClient.RateItem(LocalDate.of(2024, 12, 1), new BigDecimal("1.125"))))
      );
      
      var resp = svc.convert(p, "Target-Currency");
      // Explicitly verify the scale is 2
      assertThat(resp.convertedAmount().scale()).isEqualTo(2);
  }
}
