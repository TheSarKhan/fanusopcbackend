package com.fanus.repository;

import com.fanus.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    long countByCreatedAtAfter(LocalDateTime since);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a WHERE a.createdAt >= :since GROUP BY a.status")
    List<Object[]> countByStatusSince(@Param("since") LocalDateTime since);

    @Query("SELECT FUNCTION('DATE', a.createdAt), a.status, COUNT(a) " +
           "FROM Appointment a WHERE a.createdAt >= :since " +
           "GROUP BY FUNCTION('DATE', a.createdAt), a.status")
    List<Object[]> dailyFlowByStatus(@Param("since") LocalDateTime since);
}
