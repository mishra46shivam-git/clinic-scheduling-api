package com.clinic.scheduling_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(example = "alice.smith@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Schema(example = "securePassword123")
        String password
) {}
