package com.clinic.scheduling_api.dto;

import com.clinic.scheduling_api.entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        @Schema(example = "Alice")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Schema(example = "Smith")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(example = "alice.patient@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(example = "securePassword123")
        String password,

        @NotNull(message = "Role is required")
        @Schema(example = "ROLE_PATIENT")
        Role role,

        @Schema(description = "Phone number for patient accounts", example = "+1-555-0199")
        String phone,

        @Past(message = "Date of birth must be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1995-05-15", type = "string")
        LocalDate dateOfBirth
) {}