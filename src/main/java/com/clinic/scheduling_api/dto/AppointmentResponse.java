package com.clinic.scheduling_api.dto;

import com.clinic.scheduling_api.entity.Appointment;
import com.clinic.scheduling_api.entity.AppointmentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;

public record AppointmentResponse(
        String bookingReference,
        AppointmentStatus status,
        String patientName,
        String doctorName,
        String doctorSpecialization,
        BigDecimal fee,
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        String reasonForVisit,
        Instant createdAt
) {
    public static AppointmentResponse fromEntity(Appointment appointment) {
        var slot = appointment.getSlot();
        var doctor = slot.getDoctor();
        var patient = appointment.getPatient();

        return new AppointmentResponse(
                appointment.getBookingReference(),
                appointment.getStatus(),
                patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
                "Dr. " + doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName(),
                doctor.getSpecialization(),
                doctor.getConsultationFee(),
                slot.getStartTime(),
                slot.getEndTime(),
                appointment.getReasonForVisit(),
                appointment.getCreatedAt()
        );
    }
}
