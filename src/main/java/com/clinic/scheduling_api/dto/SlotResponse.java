package com.clinic.scheduling_api.dto;

import com.clinic.scheduling_api.entity.AppointmentSlot;
import com.clinic.scheduling_api.entity.SlotStatus;

import java.time.ZonedDateTime;

public record SlotResponse(
        Long id,
        Long doctorId,
        String doctorName,
        String doctorSpecialization,
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        SlotStatus status
) {
    public static SlotResponse fromEntity(AppointmentSlot slot) {
        var doctor = slot.getDoctor();
        var user = doctor.getUser();
        return new SlotResponse(
                slot.getId(),
                doctor.getId(),
                "Dr. " + user.getFirstName() + " " + user.getLastName(),
                doctor.getSpecialization(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus()
        );
    }
}
