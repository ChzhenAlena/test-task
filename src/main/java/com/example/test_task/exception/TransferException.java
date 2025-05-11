package com.example.test_task.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransferException extends ServiceException {

  private final UUID id;
  private final Long fromUserId;
  private final Long toUserId;
  private final BigDecimal amount;

  public TransferException(ExceptionCode code, HttpStatus status, UUID id, Long fromUserId, Long toUserId, BigDecimal amount) {
    super(code, status);
    this.id = id;
    this.fromUserId = fromUserId;
    this.toUserId = toUserId;
    this.amount = amount;
  }

  @Override
  public String getMessage() {
    return String.format("Transfer failed [id=%s from=%s, to=%s, amount=%s, reason=%s]",
        id, fromUserId, toUserId, amount, getExceptionCode().name());
  }
}

