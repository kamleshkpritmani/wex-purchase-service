package com.wex.purchase.controller;

import java.security.Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wex.purchase.dto.ConvertedPurchaseResponse;
import com.wex.purchase.dto.CreatePurchaseRequest;
import com.wex.purchase.dto.PurchaseResponse;
import com.wex.purchase.service.ConversionService;
import com.wex.purchase.service.PurchaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class PurchaseController {
  private static final Logger log = LogManager.getLogger(PurchaseController.class);

  private final PurchaseService purchaseService;
  private final ConversionService conversionService;

  public PurchaseController(PurchaseService purchaseService, ConversionService conversionService) {
    this.purchaseService = purchaseService;
    this.conversionService = conversionService;
  }

  @PostMapping
  @Operation(summary = "Create a purchase transaction in USD")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created",
          content = @Content(schema = @Schema(implementation = PurchaseResponse.class))),
      @ApiResponse(responseCode = "400", description = "Validation error")
  })
  public ResponseEntity<PurchaseResponse> create(@Valid @RequestBody CreatePurchaseRequest req, 
		    java.security.Principal principal) {
    log.debug("Create purchase request received");
    return ResponseEntity.status(201).body(purchaseService.create(req, principal.getName()));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Convert an existing purchase to a target currency using Treasury rates")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Converted",
          content = @Content(schema = @Schema(implementation = ConvertedPurchaseResponse.class))),
      @ApiResponse(responseCode = "400", description = "No rate within window"),
      @ApiResponse(responseCode = "404", description = "Purchase not found")
  })
  public ResponseEntity<ConvertedPurchaseResponse> convert(@PathVariable(name = "id") String id,
                                                           @Parameter(description = "Exact Treasury 'country_currency_desc' value, e.g., 'Australia-Dollar'", required = true)
                                                           @RequestParam("currency") String currency) {
    log.debug("Convert request purchaseId={} currency={}", id, currency);
    var purchase = purchaseService.get(id);
    return ResponseEntity.ok(conversionService.convert(purchase, currency));
  }
  
  @GetMapping("/all")
  @Operation(summary = "Get all purchases for the logged-in user")
  @ApiResponse(responseCode = "200", description = "List of user purchases")
  public ResponseEntity<Page<PurchaseResponse>> getAll(Principal principal, Pageable pageable) {
      // principal.getName() returns the username of the logged-in user
      String currentUsername = principal.getName();
      log.debug("Fetching all purchases for user: {}", currentUsername);

      return ResponseEntity.ok(purchaseService.getAllUserPurchases(currentUsername, pageable));
  }
  
}

