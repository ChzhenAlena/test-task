package com.example.test_task.service;

import com.example.test_task.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceIncreaserService {

  private final AccountRepository accountRepository;
  private final CacheManager cacheManager;

  @Value("${app.balance.update.increase-percent}")
  private int increasePercent;

  @Value("${app.balance.update.max-increase-percent}")
  private int maxIncreasePercent;

  @Transactional
  public void increaseAccountBalance(List<Long> accountIds) {
    double increaseRate = increasePercent / 100.0;
    double maxIncreaseRate = maxIncreasePercent / 100.0;

    List<Object[]> updatedAccounts = accountRepository
        .batchIncreaseBalanceWithLimit(accountIds, increaseRate, maxIncreaseRate);

    updatedAccounts.forEach(arr -> {
      //Long accountId = (Long) arr[0];
      Long userId = (Long) arr[1];
      log.debug("Balance updated for userId={}", userId);
      Objects.requireNonNull(cacheManager.getCache("accounts")).evict("user:" + userId);
    });
  }
}
