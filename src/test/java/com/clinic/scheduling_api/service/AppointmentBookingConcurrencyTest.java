package com.clinic.scheduling_api.service;

import com.clinic.scheduling_api.entity.Doctor;
import com.clinic.scheduling_api.repository.DoctorRepository;
import com.clinic.scheduling_api.dto.BookAppointmentRequest;
import com.clinic.scheduling_api.entity.AppointmentSlot;
import com.clinic.scheduling_api.entity.SlotStatus;
import com.clinic.scheduling_api.repository.AppointmentRepository;
import com.clinic.scheduling_api.repository.AppointmentSlotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppointmentBookingConcurrencyTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentSlotRepository slotRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    @DisplayName("Should allow only one booking when multiple concurrent requests target the same slot")
    void testConcurrentBookingOnSameSlot() throws InterruptedException {
        // 1. Arrange: Fetch Doctor with ID 1
        Doctor doctor = doctorRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Doctor with ID 1 was not found in the database"));

        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime start = LocalDate.now().plusDays(2).atTime(LocalTime.of(14, 0)).atZone(zoneId);
        ZonedDateTime end = LocalDate.now().plusDays(2).atTime(LocalTime.of(14, 30)).atZone(zoneId);

        // Attach doctor here so doctor_id is populated
        AppointmentSlot testSlot = slotRepository.save(
                AppointmentSlot.builder()
                        .doctor(doctor)
                        .startTime(start)
                        .endTime(end)
                        .status(SlotStatus.AVAILABLE)
                        .build()
        );
        Long slotId = testSlot.getId();
        int totalThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);
        CountDownLatch readyLatch = new CountDownLatch(totalThreads);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger failedBookings = new AtomicInteger(0);

        // 2. Act: Prepare 5 threads waiting for the starting gun
        for (int i = 0; i < totalThreads; i++) {
            long patientId = (long) (i + 1);
            executor.submit(() -> {
                readyLatch.countDown(); // signal thread is ready
                try {
                    startLatch.await(); // wait until all 5 threads are ready
                    appointmentService.bookAppointment(
                            new BookAppointmentRequest(slotId, patientId, "Concurrency load test")
                    );
                    successfulBookings.incrementAndGet();
                } catch (Exception e) {
                    failedBookings.incrementAndGet();
                }
            });
        }

        readyLatch.await(); // Wait until all 5 worker threads are lined up
        startLatch.countDown(); // Release all 5 threads simultaneously!

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(50);
        }

        // 3. Assert: Exactly 1 succeeds, exactly 4 fail, and the slot is BOOKED
        assertThat(successfulBookings.get()).isEqualTo(1);
        assertThat(failedBookings.get()).isEqualTo(totalThreads - 1);

        AppointmentSlot updatedSlot = slotRepository.findById(slotId).orElseThrow();
        assertThat(updatedSlot.getStatus()).isEqualTo(SlotStatus.BOOKED);
    }
}
