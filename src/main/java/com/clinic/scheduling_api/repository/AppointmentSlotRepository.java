package com.clinic.scheduling_api.repository;

import com.clinic.scheduling_api.entity.AppointmentSlot;
import com.clinic.scheduling_api.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    @Query("""
        SELECT s FROM AppointmentSlot s
        JOIN FETCH s.doctor d
        JOIN FETCH d.user
        WHERE s.doctor.id = :doctorId
          AND s.status = :status
          AND s.startTime >= :rangeStart
          AND s.endTime <= :rangeEnd
        ORDER BY s.startTime ASC
    """)
    List<AppointmentSlot> findAvailableSlots(
            @Param("doctorId") Long doctorId,
            @Param("status") SlotStatus status,
            @Param("rangeStart") ZonedDateTime rangeStart,
            @Param("rangeEnd") ZonedDateTime rangeEnd
    );

    @Query("""
        SELECT COUNT(s) > 0 FROM AppointmentSlot s
        WHERE s.doctor.id = :doctorId
          AND s.startTime < :newEndTime
          AND s.endTime > :newStartTime
          AND s.status != 'CANCELLED'
    """)
    boolean existsOverlappingSlot(
            @Param("doctorId") Long doctorId,
            @Param("newStartTime") ZonedDateTime newStartTime,
            @Param("newEndTime") ZonedDateTime newEndTime
    );
}
