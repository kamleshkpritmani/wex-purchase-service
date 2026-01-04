package com.wex.purchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wex.purchase.dto.CreatePurchaseRequest;
import com.wex.purchase.dto.PurchaseResponse;
import com.wex.purchase.model.Purchase;
import com.wex.purchase.repository.PurchaseRepository;

/**
 * Service for managing purchase lifecycles and retrieval.
 */
@Service
public class PurchaseService {
  private static final Logger log = LogManager.getLogger(PurchaseService.class);

  private final PurchaseRepository purchaseRepository;

  public PurchaseService(PurchaseRepository purchaseRepository) {
    this.purchaseRepository = purchaseRepository;
  }

  /**
   * Persists a new purchase transaction to the database.
   */
  @Transactional
  public PurchaseResponse create(CreatePurchaseRequest req, String username) {
    var entity = Purchase.newWithGeneratedId(username, req.description(), req.transactionDate(), req.purchaseAmountUsd());
    var saved = purchaseRepository.save(entity);
    log.info("PURCHASE_CREATED: id={}, user={}, amountUsd={}", saved.getId(), username, saved.getPurchaseAmountUsd());
    
    return mapToResponse(saved);
  }

  /**
   * Retrieves a single purchase record. Throws exception if missing for API error handling.
   */
  @Transactional(readOnly = true)
  public Purchase get(String id) {
    return purchaseRepository.findById(id)
        .orElseThrow(() -> {
            log.error("PURCHASE_NOT_FOUND: id={}", id);
            return new IllegalArgumentException("purchase not found");
        });
  }
  
  /**
   * Fetches paginated history for a specific user.
   */
  @Transactional(readOnly = true)
  public Page<PurchaseResponse> getAllUserPurchases(String username, Pageable pageable) {
    return purchaseRepository.findByUsername(username, pageable)
        .map(this::mapToResponse); 
  }
  
  private PurchaseResponse mapToResponse(Purchase entity) {
    return new PurchaseResponse(
        entity.getId(),
        entity.getDescription(),
        entity.getTransactionDate(),
        entity.getPurchaseAmountUsd()
    );
  }
}