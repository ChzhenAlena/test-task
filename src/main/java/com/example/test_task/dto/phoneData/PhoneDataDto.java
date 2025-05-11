package com.example.test_task.dto.phoneData;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PhoneDataDto (
    @NotNull
    Long id,

    @NotNull
    @Pattern(regexp = "^7\\d{10}$", message = "Phone number must start with 7 and be followed by 10 digits")
    @Schema(
        example = "79123456789",
        pattern = "^7\\d{10}$",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 11,
        maxLength = 11
    )
    String phone
) {}