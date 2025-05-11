package com.example.test_task.service;

import com.example.test_task.dto.transfer.TransferRequest;
import com.example.test_task.dto.transfer.TransferResponse;
import com.example.test_task.entity.Account;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.exception.TransferException;
import com.example.test_task.repository.AccountRepository;
import com.example.test_task.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

  @Value("${app.transfer.max-amount}")
  private BigDecimal maxTransferAmount;

  private final AccountRepository accountRepository;

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @CacheEvict(
      value = "accounts",
      key = "{'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId(), 'user:' + #transferRequest.targetUserId}"
  )
  public TransferResponse transfer(TransferRequest transferRequest) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    UUID transferId = UUID.randomUUID();
    log.info("Transfer with id={} started: from userId={} to userId={} amount={}",
        transferId, currentUserId, transferRequest.targetUserId(), transferRequest.amount());
    validateTransferAmount(transferRequest.amount(), transferId, currentUserId, transferRequest.targetUserId());

    Account currentUserAccount = accountRepository.findAccountByUserIdForUpdate(currentUserId)
            .orElseThrow(() -> new ServiceException(ExceptionCode.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND));
    Account targetUserAccount = accountRepository.findAccountByUserIdForUpdate(transferRequest.targetUserId())
        .orElseThrow(() -> new ServiceException(ExceptionCode.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND));

    validateTransferBusinessRules(transferRequest.amount(), transferId, currentUserAccount, targetUserAccount);

    currentUserAccount.setBalance((currentUserAccount.getBalance().subtract(transferRequest.amount())));
    targetUserAccount.setBalance((targetUserAccount.getBalance().add(transferRequest.amount())));
    log.info("Transfer with id={} completed: from userId={} to userId={} amount={}",
        transferId, currentUserId, transferRequest.targetUserId(), transferRequest.amount());
    return TransferResponse.success(currentUserAccount.getBalance());
  }

  private void validateTransferAmount(BigDecimal amount, UUID transferId, Long fromUserId, Long toUserId) {
    if (amount == null) {
      throw new TransferException(ExceptionCode.NULL_TRANSFER_AMOUNT, HttpStatus.BAD_REQUEST, transferId, fromUserId, toUserId, null);
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new TransferException(ExceptionCode.NON_POSITIVE_TRANSFER_AMOUNT, HttpStatus.BAD_REQUEST, transferId, fromUserId, toUserId, amount);
    }
    if (amount.compareTo(maxTransferAmount) > 0) {
      throw new TransferException(ExceptionCode.TRANSFER_LIMIT_EXCEEDED, HttpStatus.BAD_REQUEST, transferId, fromUserId, toUserId, amount);
    }
  }

  private void validateTransferBusinessRules(BigDecimal amount, UUID transferId, Account currentUserAccount, Account targetUserAccount) {
    Long fromUserId = currentUserAccount.getUser().getId();
    Long toUserId = targetUserAccount.getUser().getId();
    if (currentUserAccount.equals(targetUserAccount)) {
      throw new TransferException(ExceptionCode.SELF_TRANSFER, HttpStatus.BAD_REQUEST, transferId, fromUserId, toUserId, amount);
    }
    if (currentUserAccount.getBalance().compareTo(amount) < 0) {
      throw new TransferException(ExceptionCode.INSUFFICIENT_FUNDS, HttpStatus.BAD_REQUEST, transferId, fromUserId, toUserId, amount);
    }
  }
}
