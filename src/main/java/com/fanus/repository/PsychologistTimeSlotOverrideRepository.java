package com.fanus.repository;

import com.fanus.entity.PsychologistTimeSlotOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PsychologistTimeSlotOverrideRepository extends JpaRepository<PsychologistTimeSlotOverride, Long> {
    List<PsychologistTimeSlotOverride> findByPsychologistIdAndOverrideDateBetweenOrderByOverrideDateAscStartTimeAsc(
            Long psychologistId, LocalDate from, LocalDate to);

    List<PsychologistTimeSlotOverride> findByPsychologistIdOrderByOverrideDateDescStartTimeAsc(Long psychologistId);

    void deleteByPsychologistIdAndId(Long psychologistId, Long id);
}
