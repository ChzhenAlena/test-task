package com.example.test_task.security;

import com.example.test_task.entity.User;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUtils {

  private final UserRepository userRepository;

  public static Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ServiceException(ExceptionCode.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED);
    }

    try {
      return Long.parseLong(authentication.getName());
    } catch (NumberFormatException e) {
      throw new ServiceException(ExceptionCode.INVALID_AUTH_SUBJECT, HttpStatus.UNAUTHORIZED);
    }
  }

  public User getCurrentUser() {
    Long currentUserId = getCurrentUserId();
    return userRepository.findById(currentUserId)
        .orElseThrow(() -> new ServiceException(ExceptionCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
  }
}
