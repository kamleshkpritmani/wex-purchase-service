package com.wex.purchase.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a financial purchase record.
 * Uses JPA Auditing to automatically track creation and modification details.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "purchases")
public class Purchase {

  @Id
  private String id;
  
  @Column(nullable = false)
  private String username;

  @Column(length = 50, nullable = false)
  private String description;

  @Column(nullable = false)
  private LocalDate transactionDate;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal purchaseAmountUsd;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

  @CreatedBy
  @Column(nullable = false, updatable = false, length = 128)
  private String createdBy;

  @LastModifiedBy
  @Column(nullable = false, length = 128)
  private String updatedBy;

  public Purchase() {}

  public Purchase(String id, String username, String description, LocalDate transactionDate, BigDecimal purchaseAmountUsd) {
    this.id = id;
    this.username = username;
    this.description = description;
    this.transactionDate = transactionDate;
    this.purchaseAmountUsd = purchaseAmountUsd;
  }

  /**
   * Factory method to instantiate a Purchase with a unique identifier.
   */
  public static Purchase newWithGeneratedId(String username, String description, LocalDate date, BigDecimal amount) {
    return new Purchase(UUID.randomUUID().toString(), username, description, date, amount);
  }

  // Standard Getters/Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public LocalDate getTransactionDate() { return transactionDate; }
  public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
  public BigDecimal getPurchaseAmountUsd() { return purchaseAmountUsd; }
  public void setPurchaseAmountUsd(BigDecimal purchaseAmountUsd) { this.purchaseAmountUsd = purchaseAmountUsd; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public String getCreatedBy() { return createdBy; }
  public String getUpdatedBy() { return updatedBy; }
}