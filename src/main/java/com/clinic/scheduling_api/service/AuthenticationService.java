package com.clinic.scheduling_api.service;

import com.clinic.scheduling_api.dto.AuthenticationRequest;
import com.clinic.scheduling_api.dto.AuthenticationResponse;
import com.clinic.scheduling_api.dto.RegisterRequest;
import com.clinic.scheduling_api.entity.Patient;
import com.clinic.scheduling_api.entity.Role;
import com.clinic.scheduling_api.entity.User;
import com.clinic.scheduling_api.repository.PatientRepository;
import com.clinic.scheduling_api.repository.UserRepository;
import com.clinic.scheduling_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        User savedUser = userRepository.save(user);

        // If registered as a patient, create the corresponding Patient profile with required fields
        if (request.role() == Role.ROLE_PATIENT) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patient.setPhone(request.phone() != null ? request.phone() : "N/A");
            patient.setDateOfBirth(request.dateOfBirth() != null ? request.dateOfBirth() : LocalDate.of(2000, 1, 1));
            patientRepository.save(patient);
        }

        String jwtToken = jwtService.generateToken(savedUser);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}