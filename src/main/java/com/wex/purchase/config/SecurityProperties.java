package com.wex.purchase.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

  /**
   * URLs that should be publicly accessible in ALL environments.
   * Example: /api/v1/auth/**, /swagger-ui/**, etc.
   */
  private List<String> publicUrls = new ArrayList<>();

  /**
   * URLs that should be publicly accessible ONLY in dev/test environments
   * (e.g., h2-console).
   */
  private List<String> devOnlyPublicUrls = new ArrayList<>();

  public List<String> getPublicUrls() {
    return publicUrls;
  }

  public void setPublicUrls(List<String> publicUrls) {
    this.publicUrls = publicUrls;
  }

  public List<String> getDevOnlyPublicUrls() {
    return devOnlyPublicUrls;
  }

  public void setDevOnlyPublicUrls(List<String> devOnlyPublicUrls) {
    this.devOnlyPublicUrls = devOnlyPublicUrls;
  }
}
