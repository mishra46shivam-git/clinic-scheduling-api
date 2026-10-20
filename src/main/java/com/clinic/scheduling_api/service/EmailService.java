package com.clinic.scheduling_api.service;

import com.clinic.scheduling_api.event.AppointmentBookedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a z");

    public void sendAppointmentConfirmationEmail(AppointmentBookedEvent event) {
        String formattedStart = event.startTime().format(FORMATTER);
        String formattedEnd = event.endTime().format(FORMATTER);

        String messageContent = String.format(
                """
                Dear %s,

                Your appointment has been successfully confirmed!

                --- Details ---
                Booking Reference : %s
                Doctor            : Dr. %s (%s)
                Slot Time         : %s to %s

                Please arrive 10 minutes prior to your consultation.

                Warm regards,
                Clinical Care Team
                """,
                event.patientName(),
                event.bookingReference(),
                event.doctorName(),
                event.doctorSpecialty(),
                formattedStart,
                formattedEnd
        );

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(event.patientEmail());
            mail.setSubject("Appointment Confirmed - Ref #" + event.bookingReference());
            mail.setText(messageContent);

            mailSender.send(mail);
            log.info("Confirmation email dispatched to {}", event.patientEmail());
        } catch (Exception ex) {
            log.warn("SMTP server unreachable or dummy credentials used. Logging simulated notification:\n{}", messageContent);
        }
    }
}
