package com.fanus.service;

import com.fanus.dto.AppointmentDto;
import com.fanus.dto.AppointmentRequest;
import com.fanus.entity.Appointment;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository repo;

    @Transactional
    public AppointmentDto book(AppointmentRequest req) {
        Appointment a = Appointment.builder()
            .patientName(req.patientName()).phone(req.phone())
            .psychologistName(req.psychologistName()).note(req.note())
            .preferredDate(req.preferredDate()).build();
        return toDto(repo.save(a));
    }

    public List<AppointmentDto> findAll() {
        return repo.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    @Transactional
    public AppointmentDto updateStatus(Long id, String status) {
        Appointment a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
        a.setStatus(status);
        return toDto(repo.save(a));
    }

    private AppointmentDto toDto(Appointment a) {
        return new AppointmentDto(a.getId(), a.getPatientName(), a.getPhone(),
            a.getPsychologistName(), a.getNote(), a.getPreferredDate(), a.getStatus(), a.getCreatedAt());
    }
}
