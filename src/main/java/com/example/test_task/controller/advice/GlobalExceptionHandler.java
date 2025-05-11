package com.example.test_task.controller.advice;

import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ExceptionResponse;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.exception.TransferException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {

  @ExceptionHandler(TransferException.class)
  public ResponseEntity<ExceptionResponse> handleTransferException(TransferException ex) {
    log.warn(ex.getMessage(), ex);
    return ResponseEntity
        .status(ex.getHttpStatus())
        .body(new ExceptionResponse(
            ex.getExceptionCode(),
            ex.getExceptionCode().getMessage()));
  }

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<ExceptionResponse> handleServiceException(ServiceException ex) {
    log.warn("Service exception: {}", ex.getExceptionCode().getMessage(), ex);
    return ResponseEntity
        .status(ex.getHttpStatus())
        .body(new ExceptionResponse(ex.getExceptionCode(), ex.getExceptionCode().getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, Object> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> fieldError.getDefaultMessage() != null ?
                fieldError.getDefaultMessage() : "Invalid value"
        ));

    log.warn("Validation failed: {}", errors);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionResponse(ExceptionCode.VALIDATION_ERROR, "Validation failed"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
    log.warn("Invalid parameter value: {} = {}", ex.getName(), ex.getValue());
    String message = String.format("Parameter '%s' has invalid value '%s'", ex.getName(), ex.getValue());
    return ResponseEntity
        .badRequest()
        .body(new ExceptionResponse(ExceptionCode.VALIDATION_ERROR, message));
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    String errorMessage = "Invalid request format";
    Throwable rootCause = ex.getRootCause();
    if (rootCause instanceof JsonParseException) {
      errorMessage = "Malformed JSON request";
    } else if (rootCause instanceof InvalidFormatException ife) {
      errorMessage = String.format("Invalid value '%s' for field '%s'",
          ife.getValue(),
          ife.getPath().getLast().getFieldName());
    } else if (rootCause instanceof MismatchedInputException mie) {
      errorMessage = "Invalid input type for field: " + mie.getPath().getFirst().getFieldName();
    }
    log.warn("Invalid request: {}", errorMessage, ex);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionResponse(ExceptionCode.INVALID_REQUEST, errorMessage));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionResponse(ExceptionCode.INTERNAL_ERROR, "Internal server error"));
  }
}
