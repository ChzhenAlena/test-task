package com.example.test_task.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtUtils jwtUtils;
  private static final String AUTH_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    SecurityContextHolder.clearContext();
    try {
      String authHeader = request.getHeader(AUTH_HEADER);
      if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
        String jwtToken = authHeader.substring(BEARER_PREFIX.length());
        try {
          if (jwtUtils.isTokenValid(jwtToken)) {
            Long userId = jwtUtils.extractUserId(jwtToken);
            var authentication = new JwtAuthentication(userId);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        } catch (Exception ex) {
          SecurityContextHolder.clearContext();
        }
      }

      filterChain.doFilter(request, response);
    } finally {
      SecurityContextHolder.clearContext();
    }
  }
}