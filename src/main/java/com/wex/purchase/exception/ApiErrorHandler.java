package com.wex.purchase.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {
  private static final Logger log = LogManager.getLogger(ApiErrorHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
    log.warn("Validation failed: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of(
        "timestamp", Instant.now().toString(),
        "error", "Validation failed",
        "details", ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).toList()
    ));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String,Object>> handleStatus(ResponseStatusException ex) {
    log.warn("Request failed status={} reason={}", ex.getStatusCode(), ex.getReason());
    return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
        "timestamp", Instant.now().toString(),
        "error", ex.getReason()
    ));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,Object>> handleNotFound(IllegalArgumentException ex) {
    log.warn("Not found: {}", ex.getMessage());
    return ResponseEntity.status(404).body(Map.of(
        "timestamp", Instant.now().toString(),
        "error", ex.getMessage()
    ));
  }
}
