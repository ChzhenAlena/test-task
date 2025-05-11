package com.example.test_task.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
@Setter
public class ServiceException extends RuntimeException{
  private final ExceptionCode exceptionCode;
  private final HttpStatus httpStatus;
}
