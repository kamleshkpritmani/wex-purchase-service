package com.wex.purchase.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.purchase.external.TreasuryClient;

import reactor.core.publisher.Mono;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.sql.init.mode=always")
@Transactional // Ensures database changes are rolled back after each test
@Sql(scripts = "/db/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PurchaseControllerIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("wex")
      .withUsername("wex")
      .withPassword("wex");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
  }

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper objectMapper;

  @MockBean TreasuryClient treasuryClient;

  @Test
  void create_and_convert_happy_path() throws Exception {

    // 1) login to get token
    String tokenBody = mvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"reviewer\",\"password\":\"MyPassword123!\"}"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    JsonNode tokenJson = objectMapper.readTree(tokenBody);
    String token = tokenJson.get("token").asText();

    // 2) create purchase
    String createdBody = mvc.perform(post("/api/v1/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"description\":\"Books\",\"transactionDate\":\"2025-06-15\",\"purchaseAmountUsd\":10.00}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.description", is("Books")))
        .andReturn().getResponse().getContentAsString();

    JsonNode createdJson = objectMapper.readTree(createdBody);
    String id = createdJson.get("id").asText();

    // 3) mock treasury rates (latest <= purchaseDate within 6 months)
    when(treasuryClient.queryRates(eq("Australia-Dollar"), any(), any()))
        .thenReturn(Mono.just(List.of(
            new TreasuryClient.RateItem(LocalDate.of(2025, 3, 31), new BigDecimal("1.6")),
            new TreasuryClient.RateItem(LocalDate.of(2024, 12, 31), new BigDecimal("1.612"))
        )));

    // 4) convert
    mvc.perform(get("/api/v1/transactions/" + id)
            .header("Authorization", "Bearer " + token)
            .param("currency", "Australia-Dollar"))
        .andExpect(status().isOk())

        .andExpect(jsonPath("$.convertedAmount", is(16.00)))
        .andExpect(jsonPath("$.exchangeRateDate", is("2025-03-31")));
  }
  

}
