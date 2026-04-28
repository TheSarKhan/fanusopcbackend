package com.fanus.service;

import com.fanus.entity.Patient;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public Patient createForUser(User user) {
        Patient patient = Patient.builder()
            .user(user)
            .build();
        return patientRepository.save(patient);
    }

    public Optional<Patient> findByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }

    @Transactional
    public Patient updateDateOfBirth(Long userId, LocalDate dateOfBirth) {
        Patient patient = patientRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        patient.setDateOfBirth(dateOfBirth);
        return patientRepository.save(patient);
    }
}
