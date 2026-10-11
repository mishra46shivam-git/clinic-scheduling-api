package com.clinic.scheduling_api.repository;

import com.clinic.scheduling_api.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.id = :id")
    Optional<Doctor> findByIdWithUser(@Param("id") Long id);

    boolean existsByLicenseNumber(String licenseNumber);
}
