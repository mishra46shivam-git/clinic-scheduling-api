package com.clinic.scheduling_api.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.clinic.scheduling_api.dto.AppointmentResponse;
import com.clinic.scheduling_api.dto.CreateSlotRequest;
import com.clinic.scheduling_api.entity.*;
import com.clinic.scheduling_api.event.AppointmentBookedEvent;
import com.clinic.scheduling_api.exception.ResourceNotFoundException;
import com.clinic.scheduling_api.exception.SlotUnavailableException;
import com.clinic.scheduling_api.repository.AppointmentRepository;
import com.clinic.scheduling_api.repository.AppointmentSlotRepository;
import com.clinic.scheduling_api.repository.DoctorRepository;
import com.clinic.scheduling_api.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AppointmentSlot createSlot(CreateSlotRequest request) {
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime start = request.slotDate().atTime(request.startTime()).atZone(zoneId);
        ZonedDateTime end = request.slotDate().atTime(request.endTime()).atZone(zoneId);
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Slot end time must be strictly after start time.");
        }

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + request.doctorId()));

        boolean hasOverlap = slotRepository.existsOverlappingSlot(
                request.doctorId(),
                start,
                end
        );

        if (hasOverlap) {
            throw new SlotUnavailableException("Doctor already has an overlapping slot in this time range.");
        }

        AppointmentSlot slot = AppointmentSlot.builder()
                .doctor(doctor)
                .startTime(start)
                .endTime(end)
                .status(SlotStatus.AVAILABLE)
                .build();

        return slotRepository.save(slot);
    }

    @Transactional(rollbackFor = Exception.class)
    public AppointmentResponse bookAppointment(Long slotId, Long patientId, String reasonForVisit) {
        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with ID: " + slotId));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotUnavailableException("Slot is no longer available. Current status: " + slot.getStatus());
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.saveAndFlush(slot);

        Appointment appointment = Appointment.builder()
                .slot(slot)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .bookingReference(UUID.randomUUID().toString())
                .reasonForVisit(reasonForVisit)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created with reference: {}", savedAppointment.getBookingReference());

        // Publish domain event for asynchronous background processing
        AppointmentBookedEvent event = new AppointmentBookedEvent(
                savedAppointment.getBookingReference(),
                patient.getUser().getEmail(),
                patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
                slot.getDoctor().getUser().getFirstName() + " " + slot.getDoctor().getUser().getLastName(),
                slot.getDoctor().getSpecialization(),
                slot.getStartTime(),
                slot.getEndTime()
        );
        eventPublisher.publishEvent(event);

        return AppointmentResponse.fromEntity(savedAppointment);
    }
}