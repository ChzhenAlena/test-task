package com.example.test_task.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
    @NotNull
    @Schema(description = "Email or phone", example = "ivanov@example.com")
    String login,

    @NotNull
    @Schema(description = "Password", example = "password123")
    String password
) {}
