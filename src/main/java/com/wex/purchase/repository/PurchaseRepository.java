package com.wex.purchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wex.purchase.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, String> {
	Page<Purchase> findByUsername(String username, Pageable pageable);
}
