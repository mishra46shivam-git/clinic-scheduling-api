package com.clinic.scheduling_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record BookAppointmentRequest(
        @NotNull(message = "Slot ID is required")
        @Schema(description = "ID of the appointment slot to book", example = "1")
        Long slotId,

        @Schema(description = "Optional reason for clinic visit", example = "Routine annual physical checkup")
        String reasonForVisit
) {}