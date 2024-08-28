package com.school_system.repository;

import com.school_system.entity.school.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.attendees att  " +
            " LEFT JOIN FETCH a.organizer o WHERE" +
            " (o.id = :userId OR att.id = :userId) AND YEAR(a.startAt) = :year AND MONTH(a.startAt) = :month")
    List<Appointment> findByOrganizerOrAttendeeIdForMonth(@Param("userId")Long userId, int year, int month);
}
