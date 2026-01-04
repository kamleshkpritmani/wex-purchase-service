package com.wex.purchase.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect for auditing service-level execution and performance metrics.
 */
@Aspect
@Component
public class ServiceAuditAspect {
  private static final Logger log = LogManager.getLogger(ServiceAuditAspect.class);

  /**
   * Monitors execution time and user context for all service methods.
   */
  @Around("execution(* com.wex.purchase.service..*(..))")
  public Object auditServiceCalls(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.nanoTime();
    MethodSignature sig = (MethodSignature) pjp.getSignature();
    String method = sig.getDeclaringType().getSimpleName() + "." + sig.getName();
    String user = currentUser();

    log.info("SERVICE_AUDIT_ENTER: method={} user={}", method, user);
    try {
      Object result = pjp.proceed();
      long durationMs = (System.nanoTime() - start) / 1_000_000;
      log.info("SERVICE_AUDIT_EXIT: method={} user={} durationMs={}", method, user, durationMs);
      return result;
    } catch (Throwable t) {
      long durationMs = (System.nanoTime() - start) / 1_000_000;
      log.error("SERVICE_AUDIT_ERROR: method={} user={} durationMs={} error={}", 
          method, user, durationMs, t.getMessage());
      throw t;
    }
  }

  private String currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return "anonymous";
    return auth.getName();
  }
}