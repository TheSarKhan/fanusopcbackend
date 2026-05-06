package com.fanus.controller;

import com.fanus.dto.AppointmentDetailDto;
import com.fanus.dto.AvailableSlotDto;
import com.fanus.dto.OperatorAssignRequest;
import com.fanus.dto.PsychologistDto;
import com.fanus.dto.TimeSlotDto;
import com.fanus.dto.TimeSlotOverrideDto;
import com.fanus.dto.TimeSlotOverrideRequest;
import com.fanus.dto.TimeSlotRequest;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operator")
@PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
@RequiredArgsConstructor
public class OperatorController {

    private final AppointmentService appointmentService;
    private final PsychologistService psychologistService;
    private final PsychologistTimeSlotService timeSlotService;
    private final UserRepository userRepository;

    // ─── Triage inbox ────────────────────────────────────────────────────────

    @GetMapping("/appointments")
    public List<AppointmentDetailDto> listAll() {
        return appointmentService.findAllDetailed();
    }

    @GetMapping("/appointments/{id}")
    public AppointmentDetailDto get(@PathVariable Long id) {
        return appointmentService.findDetail(id);
    }

    @PostMapping("/appointments/{id}/assign")
    public AppointmentDetailDto assign(Authentication auth, @PathVariable Long id,
            @Valid @RequestBody OperatorAssignRequest req) {
        return appointmentService.assign(id, currentUserId(auth), req);
    }

    @PostMapping("/appointments/{id}/cancel")
    public AppointmentDetailDto cancel(@PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("note") : null;
        return appointmentService.cancelByOperator(id, note);
    }

    // ─── Psychologists list (lightweight) ────────────────────────────────────

    @GetMapping("/psychologists")
    public List<PsychologistDto> psychologists() {
        return psychologistService.findActive();
    }

    @GetMapping("/psychologists/{id}/availability")
    public List<AvailableSlotDto> availability(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return timeSlotService.availability(id, from, to);
    }

    // ─── Operator/Admin can manage any psychologist's slots ──────────────────

    @GetMapping("/psychologists/{id}/time-slots")
    public List<TimeSlotDto> psyTimeSlots(@PathVariable Long id) {
        return timeSlotService.listSlots(id);
    }

    @PostMapping("/psychologists/{id}/time-slots")
    public ResponseEntity<TimeSlotDto> createPsyTimeSlot(@PathVariable Long id,
            @Valid @RequestBody TimeSlotRequest req) {
        TimeSlotDto dto = timeSlotService.createSlot(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/psychologists/{id}/time-slots/{slotId}")
    public TimeSlotDto updatePsyTimeSlot(@PathVariable Long id, @PathVariable Long slotId,
            @Valid @RequestBody TimeSlotRequest req) {
        return timeSlotService.updateSlot(id, slotId, req);
    }

    @DeleteMapping("/psychologists/{id}/time-slots/{slotId}")
    public ResponseEntity<Void> deletePsyTimeSlot(@PathVariable Long id, @PathVariable Long slotId) {
        timeSlotService.deleteSlot(id, slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/psychologists/{id}/time-slot-overrides")
    public List<TimeSlotOverrideDto> psyOverrides(@PathVariable Long id) {
        return timeSlotService.listOverrides(id);
    }

    @PostMapping("/psychologists/{id}/time-slot-overrides")
    public ResponseEntity<TimeSlotOverrideDto> createPsyOverride(@PathVariable Long id,
            @Valid @RequestBody TimeSlotOverrideRequest req) {
        TimeSlotOverrideDto dto = timeSlotService.createOverride(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/psychologists/{id}/time-slot-overrides/{overrideId}")
    public ResponseEntity<Void> deletePsyOverride(@PathVariable Long id, @PathVariable Long overrideId) {
        timeSlotService.deleteOverride(id, overrideId);
        return ResponseEntity.noContent().build();
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private Long currentUserId(Authentication auth) {
        String email = auth.getName();
        User u = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return u.getId();
    }
}
