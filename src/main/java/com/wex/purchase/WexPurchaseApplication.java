package com.wex.purchase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.wex.purchase.config.SecurityProperties;

/**
 * Main Entry point for the Wex Purchase Service.
 * * Key Features:
 * - Caching: Enabled to optimize Treasury API calls.
 * - JPA Auditing: Automatically populates createdAt/By and updatedAt/By fields.
 * - SecurityProperties: Type-safe configuration for JWT and user settings.
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableConfigurationProperties(SecurityProperties.class)
public class WexPurchaseApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(WexPurchaseApplication.class, args);
  }
}