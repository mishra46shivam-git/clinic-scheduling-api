package com.clinic.scheduling_api.event;

import java.time.ZonedDateTime;

public record AppointmentBookedEvent(
        String bookingReference,
        String patientEmail,
        String patientName,
        String doctorName,
        String doctorSpecialty,
        ZonedDateTime startTime,
        ZonedDateTime endTime
) {}
