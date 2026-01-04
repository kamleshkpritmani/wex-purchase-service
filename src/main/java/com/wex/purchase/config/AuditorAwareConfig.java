package com.wex.purchase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditorAwareConfig {
  @Bean
  public AuditorAware<String> auditorAware() {
	  
	  
    return () -> {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || !auth.isAuthenticated()) return Optional.of("system");
      Object principal = auth.getPrincipal();
      if (principal == null) return Optional.of("system");
      return Optional.of(String.valueOf(principal));
    };
  }
  
}
