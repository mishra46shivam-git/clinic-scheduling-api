package com.clinic.scheduling_api.controller;

import com.clinic.scheduling_api.dto.AppointmentResponse;
import com.clinic.scheduling_api.dto.BookAppointmentRequest;
import com.clinic.scheduling_api.exception.ResourceNotFoundException;
import com.clinic.scheduling_api.repository.AppointmentRepository;
import com.clinic.scheduling_api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody BookAppointmentRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{bookingReference}")
    public ResponseEntity<AppointmentResponse> getByReference(@PathVariable String bookingReference) {
        AppointmentResponse response = appointmentRepository.findByBookingReference(bookingReference)
                .map(AppointmentResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("No appointment found for reference: " + bookingReference));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(@PathVariable Long patientId) {
        List<AppointmentResponse> history = appointmentRepository.findAllByPatientIdWithDetails(patientId)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(history);
    }
}
