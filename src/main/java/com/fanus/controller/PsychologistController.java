package com.fanus.controller;

import com.fanus.dto.AppointmentDetailDto;
import com.fanus.dto.PsychologistDto;
import com.fanus.dto.TimeSlotDto;
import com.fanus.dto.TimeSlotOverrideDto;
import com.fanus.dto.TimeSlotOverrideRequest;
import com.fanus.dto.TimeSlotRequest;
import com.fanus.entity.Psychologist;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.UserRepository;
import com.fanus.service.AppointmentService;
import com.fanus.service.PsychologistService;
import com.fanus.service.PsychologistTimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/psychologist")
@PreAuthorize("hasRole('PSYCHOLOGIST')")
@RequiredArgsConstructor
public class PsychologistController {

    private final UserRepository userRepository;
    private final PsychologistService psychologistService;
    private final PsychologistTimeSlotService timeSlotService;
    private final AppointmentService appointmentService;

    // ─── Profile lookup ──────────────────────────────────────────────────────

    @GetMapping("/me")
    public PsychologistDto me(Authentication auth) {
        Psychologist p = currentPsychologist(auth);
        return psychologistService.findById(p.getId());
    }

    @PutMapping("/me/session-minutes")
    public PsychologistDto updateSessionMinutes(Authentication auth, @RequestBody Map<String, Integer> body) {
        Integer minutes = body != null ? body.get("minutes") : null;
        if (minutes == null) throw new IllegalArgumentException("minutes tələb olunur");
        return psychologistService.updateSessionMinutes(currentPsychologist(auth).getId(), minutes);
    }

    // ─── Weekly time slots ───────────────────────────────────────────────────

    @GetMapping("/time-slots")
    public List<TimeSlotDto> listSlots(Authentication auth) {
        return timeSlotService.listSlots(currentPsychologist(auth).getId());
    }

    @PostMapping("/time-slots")
    public ResponseEntity<TimeSlotDto> createSlot(Authentication auth, @Valid @RequestBody TimeSlotRequest req) {
        TimeSlotDto dto = timeSlotService.createSlot(currentPsychologist(auth).getId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/time-slots/{slotId}")
    public TimeSlotDto updateSlot(Authentication auth, @PathVariable Long slotId, @Valid @RequestBody TimeSlotRequest req) {
        return timeSlotService.updateSlot(currentPsychologist(auth).getId(), slotId, req);
    }

    @DeleteMapping("/time-slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(Authentication auth, @PathVariable Long slotId) {
        timeSlotService.deleteSlot(currentPsychologist(auth).getId(), slotId);
        return ResponseEntity.noContent().build();
    }

    // ─── Date overrides ──────────────────────────────────────────────────────

    @GetMapping("/time-slot-overrides")
    public List<TimeSlotOverrideDto> listOverrides(Authentication auth) {
        return timeSlotService.listOverrides(currentPsychologist(auth).getId());
    }

    @PostMapping("/time-slot-overrides")
    public ResponseEntity<TimeSlotOverrideDto> createOverride(Authentication auth,
            @Valid @RequestBody TimeSlotOverrideRequest req) {
        TimeSlotOverrideDto dto = timeSlotService.createOverride(currentPsychologist(auth).getId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/time-slot-overrides/{overrideId}")
    public ResponseEntity<Void> deleteOverride(Authentication auth, @PathVariable Long overrideId) {
        timeSlotService.deleteOverride(currentPsychologist(auth).getId(), overrideId);
        return ResponseEntity.noContent().build();
    }

    // ─── Appointments ────────────────────────────────────────────────────────

    @GetMapping("/appointments")
    public List<AppointmentDetailDto> myAppointments(Authentication auth) {
        return appointmentService.findForPsychologistUser(currentUserId(auth));
    }

    @PostMapping("/appointments/{id}/confirm")
    public AppointmentDetailDto confirm(Authentication auth, @PathVariable Long id) {
        return appointmentService.confirmByPsychologist(id, currentUserId(auth));
    }

    @PostMapping("/appointments/{id}/reject")
    public AppointmentDetailDto reject(Authentication auth, @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("note") : null;
        return appointmentService.rejectByPsychologist(id, currentUserId(auth), note);
    }

    @PostMapping("/appointments/{id}/complete")
    public AppointmentDetailDto complete(Authentication auth, @PathVariable Long id) {
        return appointmentService.completeByPsychologist(id, currentUserId(auth));
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private Long currentUserId(Authentication auth) {
        String email = auth.getName();
        User u = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return u.getId();
    }

    private Psychologist currentPsychologist(Authentication auth) {
        return psychologistService.requireByUserId(currentUserId(auth));
    }
}
