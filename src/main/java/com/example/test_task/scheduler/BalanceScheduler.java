package com.example.test_task.scheduler;

import com.example.test_task.repository.AccountRepository;
import com.example.test_task.service.BalanceIncreaserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceScheduler {

  @Value("${app.balance.update.rate-ms}")
  private long updateRateMs;

  private final BalanceIncreaserService balanceIncreaserService;
  private final AccountRepository accountRepository;

  @Scheduled(fixedRateString = "${app.balance.update.rate-ms}")
  public void scheduleBalanceIncrease() {
    int pageSize = 500;
    Pageable pageable = PageRequest.of(0, pageSize);
    Page<Long> page;

    do {
      page = accountRepository.findAllAccountIds(pageable);
      List<Long> accountIds = page.getContent();
      if (!accountIds.isEmpty()) {
        balanceIncreaserService.increaseAccountBalance(accountIds);
      }

      pageable = pageable.next();
    } while (page.hasNext());
  }
}