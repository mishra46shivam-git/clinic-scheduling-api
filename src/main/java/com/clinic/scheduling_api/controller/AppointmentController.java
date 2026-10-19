package com.clinic.scheduling_api.controller;

import com.clinic.scheduling_api.dto.AppointmentResponse;
import com.clinic.scheduling_api.dto.BookAppointmentRequest;
import com.clinic.scheduling_api.entity.Patient;
import com.clinic.scheduling_api.exception.ResourceNotFoundException;
import com.clinic.scheduling_api.repository.PatientRepository;
import com.clinic.scheduling_api.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Endpoints for appointment lifecycle operations")
@SecurityRequirement(name = "BearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;

    @PostMapping("/book")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Book an appointment slot (Patient only)")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request,
            Principal principal
    ) {
        Patient patient = patientRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user: " + principal.getName()));

        AppointmentResponse response = appointmentService.bookAppointment(
                request.slotId(),
                patient.getId(),
                request.reasonForVisit()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}