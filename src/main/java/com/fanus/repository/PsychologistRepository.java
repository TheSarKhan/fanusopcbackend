package com.fanus.repository;

import com.fanus.entity.Psychologist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PsychologistRepository extends JpaRepository<Psychologist, Long> {
    List<Psychologist> findByActiveTrueOrderByDisplayOrderAsc();
    List<Psychologist> findAllByOrderByIdAsc();

    long countByActiveTrue();

    boolean existsByEmail(String email);
    Optional<Psychologist> findByEmail(String email);
    Optional<Psychologist> findByUserId(Long userId);
}
