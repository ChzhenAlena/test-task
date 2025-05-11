package com.example.test_task.dto.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
    String status,
    BigDecimal currentBalance,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "Europe/Moscow")
    Instant timestamp
) {
    public static TransferResponse success(BigDecimal newBalance) {
        return new TransferResponse("COMPLETED", newBalance, Instant.now());
    }
}