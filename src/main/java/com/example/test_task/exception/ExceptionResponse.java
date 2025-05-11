package com.example.test_task.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class ExceptionResponse {
  private final ExceptionCode code;
  private final String message;
  private final Instant timestamp = Instant.now();
}