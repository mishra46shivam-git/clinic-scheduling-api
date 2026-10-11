package com.clinic.scheduling_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSlotRequest(
        @NotNull(message = "Doctor ID is required")
        Long doctorId,

        @NotNull(message = "Slot date is required")
        @FutureOrPresent(message = "Slot date cannot be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate slotDate,

        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime
) {}