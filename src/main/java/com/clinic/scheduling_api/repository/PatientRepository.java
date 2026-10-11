package com.clinic.scheduling_api.repository;

import com.clinic.scheduling_api.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p FROM Patient p JOIN FETCH p.user WHERE p.id = :id")
    Optional<Patient> findByIdWithUser(@Param("id") Long id);
}
