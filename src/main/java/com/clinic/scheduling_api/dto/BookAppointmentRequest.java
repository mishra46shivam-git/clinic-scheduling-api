package com.clinic.scheduling_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookAppointmentRequest(
        @NotNull(message = "Slot ID is required")
        Long slotId,

        @NotNull(message = "Patient ID is required")
        Long patientId,

        @NotBlank(message = "Reason for visit is required")
        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reasonForVisit
) {}
