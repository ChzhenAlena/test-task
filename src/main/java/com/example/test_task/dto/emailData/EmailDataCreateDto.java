package com.example.test_task.dto.emailData;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmailDataCreateDto (
    @NotNull
    @Email
    @Size(max = 200, message = "Email length should be less than 200")
    @Schema(
        example = "user@example.com",
        maxLength = 200,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email
) {}