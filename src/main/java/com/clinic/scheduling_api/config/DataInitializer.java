package com.clinic.scheduling_api.config;

import com.clinic.scheduling_api.entity.*;
import com.clinic.scheduling_api.repository.DoctorRepository;
import com.clinic.scheduling_api.repository.PatientRepository;
import com.clinic.scheduling_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User docUser = userRepository.save(User.builder()
                .email("doctor.house@clinic.com")
                .passwordHash("hashed_password_123")
                .firstName("Gregory")
                .lastName("House")
                .role(Role.ROLE_DOCTOR)
                .build());

        doctorRepository.save(Doctor.builder()
                .user(docUser)
                .specialization("Diagnostics")
                .licenseNumber("MD-884920")
                .consultationFee(BigDecimal.valueOf(150.00))
                .build());

        User patientUser = userRepository.save(User.builder()
                .email("john.doe@gmail.com")
                .passwordHash("hashed_password_123")
                .firstName("John")
                .lastName("Doe")
                .role(Role.ROLE_PATIENT)
                .build());

        patientRepository.save(Patient.builder()
                .user(patientUser)
                .phone("+1-555-0199")
                .dateOfBirth(LocalDate.of(1996, 4, 12))
                .build());
    }
}
