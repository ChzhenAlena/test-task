package com.example.test_task.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class JwtAuthentication implements Authentication {
  private final Long userId;
  private boolean authenticated = true;

  public JwtAuthentication(Long userId) {
    this.userId = userId;
  }

  @Override
  public Long getPrincipal() {
    return userId;
  }

  @Override public Object getCredentials() { return null; }
  @Override public Object getDetails() { return null; }
  @Override public String getName() { return userId.toString(); }
  @Override public Collection<? extends GrantedAuthority> getAuthorities() { return Collections.emptyList(); }
  @Override public boolean isAuthenticated() { return authenticated; }
  @Override public void setAuthenticated(boolean isAuthenticated) { this.authenticated = isAuthenticated; }
}