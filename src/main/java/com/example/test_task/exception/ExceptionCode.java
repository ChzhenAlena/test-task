package com.example.test_task.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {
  INTERNAL_ERROR("Internal server error"),
  NOT_FOUND("Resource not found"),
  VALIDATION_ERROR("Validation failed"),
  FORBIDDEN("Forbidden"),
  AUTHENTICATION_FAILED("Invalid credentials"),
  UNAUTHENTICATED("User not authenticated"),
  INVALID_AUTH_SUBJECT("Invalid user ID in authentication"),
  INVALID_REQUEST("Invalid request format"),

  INSUFFICIENT_FUNDS("Insufficient funds"),
  DUPLICATE_EMAIL("Email already exists"),
  DUPLICATE_PHONE("Phone number already exists"),
  DELETING_LAST_EMAIL("User should have at least one email"),
  DELETING_LAST_PHONE("User should have at least one phone number"),
  USER_NOT_FOUND("User not found"),
  ACCOUNT_NOT_FOUND("Account not found"),
  SELF_TRANSFER("Cannot transfer to yourself"),
  TRANSFER_LIMIT_EXCEEDED("Transfer amount exceeds limit"),
  NULL_TRANSFER_AMOUNT("Transfer amount cannot be null"),
  NON_POSITIVE_TRANSFER_AMOUNT("Transfer amount should be positive");

  private final String message;
}
