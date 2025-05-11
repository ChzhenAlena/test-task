package com.example.test_task.dto.transfer;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest (
    @NotNull
    Long targetUserId,
    @NotNull
    @Positive
    @Digits(integer = 19, fraction = 2, message = "Amount must have 2 decimal places")
    BigDecimal amount
) {}
