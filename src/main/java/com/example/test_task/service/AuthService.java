package com.example.test_task.service;

import com.example.test_task.dto.auth.AuthRequest;
import com.example.test_task.dto.auth.AuthResponse;
import com.example.test_task.entity.User;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.repository.UserRepository;
import com.example.test_task.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;

  public AuthResponse login(AuthRequest request) {
    log.debug("Attempting to log in user");
    User user = authenticate(request.login(), request.password());
    String token = jwtUtils.generateToken(user.getId());
    log.debug("User with ID {} successfully authenticated", user.getId());
    return new AuthResponse(token);
  }

  @Transactional(readOnly = true)
  public User authenticate(String login, String password) {
    User user = userRepository.findByEmailOrPhone(login, login)
        .orElseThrow(() -> {
          return new ServiceException(ExceptionCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
        });
    if (!passwordEncoder.matches(password, user.getPassword())) {
      log.warn("Authentication failed: invalid password for user ID {}", user.getId());
      throw new ServiceException(ExceptionCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
    }
    return user;
  }
}
