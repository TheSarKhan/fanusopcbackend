package com.fanus.controller;

import com.fanus.dto.AppointmentDetailDto;
import com.fanus.dto.PatientBookingRequest;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.UserRepository;
import com.fanus.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@PreAuthorize("hasRole('PATIENT')")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @GetMapping("/appointments")
    public List<AppointmentDetailDto> myAppointments(Authentication auth) {
        return appointmentService.findForPatientUser(currentUserId(auth));
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDetailDto> book(
            Authentication auth,
            @Valid @RequestBody PatientBookingRequest req) {
        AppointmentDetailDto created = appointmentService.bookForPatient(currentUserId(auth), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/appointments/{id}/cancel")
    public AppointmentDetailDto cancel(Authentication auth, @PathVariable Long id) {
        return appointmentService.cancelByPatient(id, currentUserId(auth));
    }

    private Long currentUserId(Authentication auth) {
        String email = auth.getName();
        User u = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return u.getId();
    }
}
