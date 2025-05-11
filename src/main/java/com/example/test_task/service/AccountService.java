package com.example.test_task.service;

import com.example.test_task.dto.accountData.AccountDto;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.mapper.AccountMapper;
import com.example.test_task.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;

  @Cacheable(
      value = "accounts",
      key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"
  )
  @Transactional(readOnly = true)
  public AccountDto getByUserId(Long userId) {
    return accountRepository.findByUserId(userId)
        .map(accountMapper::toDto)
        .orElseThrow(() -> new ServiceException(ExceptionCode.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND));
  }
}
