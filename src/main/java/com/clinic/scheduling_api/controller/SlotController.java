package com.clinic.scheduling_api.controller;

import com.clinic.scheduling_api.dto.CreateSlotRequest;
import com.clinic.scheduling_api.dto.SlotResponse;
import com.clinic.scheduling_api.entity.AppointmentSlot;
import com.clinic.scheduling_api.entity.SlotStatus;
import com.clinic.scheduling_api.repository.AppointmentSlotRepository;
import com.clinic.scheduling_api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
public class SlotController {

    private final AppointmentService appointmentService;
    private final AppointmentSlotRepository slotRepository;

    @PostMapping
    public ResponseEntity<SlotResponse> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        AppointmentSlot createdSlot = appointmentService.createSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SlotResponse.fromEntity(createdSlot));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end
    ) {
        List<SlotResponse> slots = slotRepository
                .findAvailableSlots(doctorId, SlotStatus.AVAILABLE, start, end)
                .stream()
                .map(SlotResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(slots);
    }
}
