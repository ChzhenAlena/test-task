package com.example.test_task.controller;

import com.example.test_task.dto.auth.AuthRequest;
import com.example.test_task.dto.auth.AuthResponse;
import com.example.test_task.security.JwtUtils;
import com.example.test_task.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final JwtUtils jwtUtils;

  @PostMapping("/login")
  @Operation(summary = "Login with email/phone and password")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse login(@RequestBody @Valid AuthRequest request) {
    return authService.login(request);
  }
}