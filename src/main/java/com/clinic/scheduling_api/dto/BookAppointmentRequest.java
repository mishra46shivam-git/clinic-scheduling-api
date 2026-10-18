package com.clinic.scheduling_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookAppointmentRequest(
        @NotNull(message = "Slot ID is required")
        @Schema(description = "ID of the target slot", example = "1")
        Long slotId,

        @NotNull(message = "Patient ID is required")
        @Schema(description = "ID of the patient booking the slot", example = "1")
        Long patientId,

        @NotBlank(message = "Reason for visit is required")
        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        @Schema(description = "Reason for visit", example = "Routine general checkup")
        String reasonForVisit
) {}