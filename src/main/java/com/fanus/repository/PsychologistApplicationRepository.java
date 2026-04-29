package com.fanus.repository;

import com.fanus.entity.PsychologistApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PsychologistApplicationRepository extends JpaRepository<PsychologistApplication, Long> {
    boolean existsByEmail(String email);
    Optional<PsychologistApplication> findByEmail(String email);
}
