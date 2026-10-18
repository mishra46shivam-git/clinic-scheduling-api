package com.clinic.scheduling_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSlotRequest(
        @NotNull(message = "Doctor ID is required")
        @Schema(description = "ID of the doctor", example = "1")
        Long doctorId,

        @NotNull(message = "Slot date is required")
        @FutureOrPresent(message = "Slot date cannot be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Date of the appointment slot (YYYY-MM-DD)", example = "2026-10-25", type = "string")
        LocalDate slotDate,

        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "Start time in HH:mm 24-hr format", example = "10:00", type = "string")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "End time in HH:mm 24-hr format", example = "10:30", type = "string")
        LocalTime endTime
) {}