package com.example.test_task.service;

import com.example.test_task.dto.user.UserFullDto;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.mapper.UserMapper;
import com.example.test_task.repository.UserRepository;
import com.example.test_task.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoCacheService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Cacheable(
      value = "users",
      key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"
  )
  public UserFullDto getCurrentUserInfoWithoutBalance() {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    return userRepository.findById(currentUserId)
        .map(userMapper::toFullDto)
        .orElseThrow(() -> new ServiceException(ExceptionCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
  }
}
