package com.wex.purchase.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final Key key;
  private final long ttlSeconds;
  private final String issuer;

  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.ttl-seconds:1800}") long ttlSeconds,   // default 30 min
      @Value("${security.jwt.issuer:wex-purchase-service}") String issuer
  ) {
	  
    // HS256 requires >= 32 bytes secret
    if (secret == null || secret.trim().length() < 32) {
      throw new IllegalArgumentException("security.jwt.secret must be at least 32 characters for HS256");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.ttlSeconds = ttlSeconds;
    this.issuer = issuer;
  }

  public String generate(String username) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(ttlSeconds);

    return Jwts.builder()
        .setSubject(username)
        .setIssuer(issuer)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String validateAndGetSubject(String token) {
    JwtParser parser = Jwts.parserBuilder()
        .setSigningKey(key)
        .requireIssuer(issuer)
        .build();

    Jws<Claims> jws = parser.parseClaimsJws(token);
    return jws.getBody().getSubject();
  }
}
