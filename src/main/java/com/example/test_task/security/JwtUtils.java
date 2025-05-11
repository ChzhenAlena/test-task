package com.example.test_task.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {
  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration-ms}")
  private long expirationMs;

  public String generateToken(Long userId) {
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getSigningKey())
        .compact();
  }

  public Long extractUserId(String token) {
    return Long.parseLong(Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject());
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public boolean isTokenValid(String token) {
    try {
      if (token == null || token.isEmpty()) {
        return false;
      }
      var claims = Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
      Date expiration = claims.getExpiration();
      if (expiration.before(new Date())) {
        return false;
      }
      if (claims.getSubject() == null || claims.getSubject().isEmpty()) {
        return false;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}