package com.clinic.scheduling_api.repository;

import com.clinic.scheduling_api.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByBookingReference(String bookingReference);

    @Query("""
        SELECT a FROM Appointment a
        JOIN FETCH a.slot s
        JOIN FETCH s.doctor d
        JOIN FETCH d.user
        JOIN FETCH a.patient p
        JOIN FETCH p.user
        WHERE a.patient.id = :patientId
        ORDER BY s.startTime DESC
    """)
    List<Appointment> findAllByPatientIdWithDetails(@Param("patientId") Long patientId);
}
