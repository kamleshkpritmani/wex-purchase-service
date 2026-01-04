package com.wex.purchase.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wex.purchase.config.SecurityProperties;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final SecurityProperties securityProperties;
  private final Environment environment;

  public SecurityConfig(
      JwtAuthFilter jwtAuthFilter,
      SecurityProperties securityProperties,
      Environment environment
  ) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.securityProperties = securityProperties;
    this.environment = environment;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // Build final allow list based on profile
    List<String> permitAll = new ArrayList<>();
    permitAll.addAll(securityProperties.getPublicUrls());

    if (isDevOrTest()) {
      permitAll.addAll(securityProperties.getDevOnlyPublicUrls());
    }

    String[] permitAllArray = permitAll.toArray(new String[0]);

    http
        // Stateless REST API: CSRF off (only do this when NOT using cookies for auth)
        .csrf(csrf -> csrf.disable())

        // H2 console uses frames; allow same-origin frames
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(Customizer.withDefaults())

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(permitAllArray).permitAll()
            .requestMatchers("/api/v1/transactions/**").hasRole("USER")
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  private boolean isDevOrTest() {
    for (String p : environment.getActiveProfiles()) {
      if ("dev".equalsIgnoreCase(p) || "test".equalsIgnoreCase(p)) {
        return true;
      }
    }
    return false;
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  
}
