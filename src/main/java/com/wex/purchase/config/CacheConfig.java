package com.wex.purchase.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
	
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager mgr = new CaffeineCacheManager("rates");
    mgr.setCaffeine(Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(6, TimeUnit.HOURS));
    return mgr;
  }
  
}
