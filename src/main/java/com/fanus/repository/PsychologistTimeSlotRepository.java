package com.fanus.repository;

import com.fanus.entity.PsychologistTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PsychologistTimeSlotRepository extends JpaRepository<PsychologistTimeSlot, Long> {
    List<PsychologistTimeSlot> findByPsychologistIdOrderByDayOfWeekAscStartTimeAsc(Long psychologistId);

    List<PsychologistTimeSlot> findByPsychologistIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(Long psychologistId);

    void deleteByPsychologistIdAndId(Long psychologistId, Long id);
}
