package com.example.test_task.controller;

import com.example.test_task.dto.transfer.TransferRequest;
import com.example.test_task.dto.transfer.TransferResponse;
import com.example.test_task.service.TransferService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public TransferResponse transfer(@RequestBody @Valid TransferRequest transferRequest) {
    return transferService.transfer(transferRequest);
  }

}
