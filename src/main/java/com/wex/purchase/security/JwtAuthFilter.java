package com.wex.purchase.security;

import static com.wex.purchase.security.SecurityConstants.AUTH_HEADER;
import static com.wex.purchase.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final Logger log = LogManager.getLogger(JwtAuthFilter.class);
  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
	super();
	this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String header = request.getHeader(AUTH_HEADER);

    if (header != null && header.startsWith(TOKEN_PREFIX)) {
      String token = header.substring(TOKEN_PREFIX.length()).trim();

      try {
        String username = jwtService.validateAndGetSubject(token);

        var auth = new UsernamePasswordAuthenticationToken(
            username,
            null,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (JwtException | IllegalArgumentException ex) {
        // Token invalid/expired -> clear context, let Security handle as 401
        log.warn("JWT validation failed: {}", ex.getMessage());
        SecurityContextHolder.clearContext();
      }
    }

    chain.doFilter(request, response);
  }
  
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
      String path = request.getServletPath();

      return path.startsWith("/api/v1/auth/") || 
             path.startsWith("/actuator/") || 
             path.startsWith("/swagger-ui") || 
             path.startsWith("/v3/api-docs");
  }
  
}
