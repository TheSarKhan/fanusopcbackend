package com.fanus.service;

import com.fanus.dto.AppointmentDetailDto;
import com.fanus.dto.AppointmentDto;
import com.fanus.dto.AppointmentRequest;
import com.fanus.dto.OperatorAssignRequest;
import com.fanus.dto.PatientBookingRequest;
import com.fanus.entity.Appointment;
import com.fanus.entity.Patient;
import com.fanus.entity.Psychologist;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.AppointmentRepository;
import com.fanus.repository.PatientRepository;
import com.fanus.repository.PsychologistRepository;
import com.fanus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository repo;
    private final PatientRepository patientRepository;
    private final PsychologistRepository psychologistRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // ─── Legacy anonymous booking (kept for old landing/booking modal) ───────

    @Transactional
    public AppointmentDto book(AppointmentRequest req) {
        Appointment a = Appointment.builder()
            .patientName(req.patientName()).phone(req.phone())
            .psychologistName(req.psychologistName()).note(req.note())
            .preferredDate(req.preferredDate()).build();
        a = repo.save(a);
        return toLegacyDto(a);
    }

    public List<AppointmentDto> findAll() {
        return repo.findAllByOrderByCreatedAtDesc().stream().map(this::toLegacyDto).toList();
    }

    @Transactional
    public AppointmentDto updateStatus(Long id, String status) {
        Appointment a = require(id);
        a.setStatus(status);
        return toLegacyDto(repo.save(a));
    }

    // ─── New flow: list views ────────────────────────────────────────────────

    public List<AppointmentDetailDto> findAllDetailed() {
        return repo.findAllByOrderByCreatedAtDesc().stream().map(this::toDetailDto).toList();
    }

    public List<AppointmentDetailDto> findForPatientUser(Long userId) {
        Patient p = patientRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return repo.findByPatientIdOrderByCreatedAtDesc(p.getId()).stream().map(this::toDetailDto).toList();
    }

    public List<AppointmentDetailDto> findForPsychologistUser(Long userId) {
        Psychologist psy = psychologistRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Psychologist profile not found"));
        return repo.findByPsychologistIdOrderByStartAtAsc(psy.getId()).stream().map(this::toDetailDto).toList();
    }

    public AppointmentDetailDto findDetail(Long id) {
        return toDetailDto(require(id));
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @Transactional
    public AppointmentDetailDto bookForPatient(Long userId, PatientBookingRequest req) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Patient patient = patientRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        Psychologist requested = null;
        if (req.requestedPsychologistId() != null) {
            requested = psychologistRepository.findById(req.requestedPsychologistId())
                .orElseThrow(() -> new ResourceNotFoundException("Requested psychologist not found"));
        }

        Appointment a = Appointment.builder()
            .patient(patient)
            .patientName(fullName(user))
            .phone(user.getPhone())
            .requestedPsychologist(requested)
            .psychologistName(requested != null ? requested.getName() : null)
            .requestedStartAt(req.requestedStartAt())
            .sessionFormat(req.sessionFormat())
            .note(req.note())
            .status("PENDING")
            .build();
        a = repo.save(a);

        final Psychologist finalRequested = requested;
        safeSend(() -> emailService.sendAppointmentReceived(
            user.getEmail(), firstNameOrEmail(user),
            finalRequested != null ? finalRequested.getName() : null,
            req.requestedStartAt()));

        return toDetailDto(a);
    }

    @Transactional
    public AppointmentDetailDto assign(Long appointmentId, Long operatorUserId, OperatorAssignRequest req) {
        Appointment a = require(appointmentId);
        Psychologist psy = psychologistRepository.findById(req.psychologistId())
            .orElseThrow(() -> new ResourceNotFoundException("Psychologist not found"));
        User operator = userRepository.findById(operatorUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Operator not found"));

        if (req.endAt() == null || req.startAt() == null || !req.startAt().isBefore(req.endAt())) {
            throw new IllegalArgumentException("startAt must be before endAt");
        }

        a.setPsychologist(psy);
        a.setPsychologistName(psy.getName());
        a.setStartAt(req.startAt());
        a.setEndAt(req.endAt());
        if (req.sessionFormat() != null) a.setSessionFormat(req.sessionFormat());
        if (req.operatorNote() != null) a.setOperatorNote(req.operatorNote());
        a.setAssignedByOperator(operator);
        a.setStatus("ASSIGNED");
        a = repo.save(a);

        // Notify the patient (if linked) and the psychologist
        notifyAssigned(a);
        return toDetailDto(a);
    }

    @Transactional
    public AppointmentDetailDto confirmByPsychologist(Long appointmentId, Long psychologistUserId) {
        Appointment a = require(appointmentId);
        ensureBelongsToPsychologistUser(a, psychologistUserId);
        if (!"ASSIGNED".equals(a.getStatus()) && !"PENDING".equals(a.getStatus())) {
            throw new IllegalStateException("Only ASSIGNED appointments can be confirmed");
        }
        a.setStatus("CONFIRMED");
        a = repo.save(a);
        notifyConfirmed(a);
        return toDetailDto(a);
    }

    @Transactional
    public AppointmentDetailDto rejectByPsychologist(Long appointmentId, Long psychologistUserId, String note) {
        Appointment a = require(appointmentId);
        ensureBelongsToPsychologistUser(a, psychologistUserId);
        if (note != null && !note.isBlank()) a.setOperatorNote(note);
        // Clear assignment so operator can reassign — status REJECTED keeps it in the triage tab
        a.setStartAt(null);
        a.setEndAt(null);
        a.setPsychologist(null);
        a.setPsychologistName(null);
        a.setStatus("REJECTED");
        a = repo.save(a);
        notifyRejected(a);
        return toDetailDto(a);
    }

    @Transactional
    public AppointmentDetailDto completeByPsychologist(Long appointmentId, Long psychologistUserId) {
        Appointment a = require(appointmentId);
        ensureBelongsToPsychologistUser(a, psychologistUserId);
        if (!"CONFIRMED".equals(a.getStatus())) {
            throw new IllegalStateException("Only CONFIRMED appointments can be completed");
        }
        a.setStatus("COMPLETED");
        return toDetailDto(repo.save(a));
    }

    @Transactional
    public AppointmentDetailDto cancelByPatient(Long appointmentId, Long patientUserId) {
        Appointment a = require(appointmentId);
        if (a.getPatient() == null || a.getPatient().getUser() == null
                || !a.getPatient().getUser().getId().equals(patientUserId)) {
            throw new ResourceNotFoundException("Appointment not found for this patient");
        }
        if ("COMPLETED".equals(a.getStatus()) || "CANCELLED".equals(a.getStatus())) {
            throw new IllegalStateException("Appointment already finalised");
        }
        a.setStatus("CANCELLED");
        a = repo.save(a);
        notifyCancelled(a, "PATIENT");
        return toDetailDto(a);
    }

    @Transactional
    public AppointmentDetailDto cancelByOperator(Long appointmentId, String operatorNote) {
        Appointment a = require(appointmentId);
        a.setStatus("CANCELLED");
        if (operatorNote != null && !operatorNote.isBlank()) a.setOperatorNote(operatorNote);
        a = repo.save(a);
        notifyCancelled(a, "OPERATOR");
        return toDetailDto(a);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private void ensureBelongsToPsychologistUser(Appointment a, Long psychologistUserId) {
        if (a.getPsychologist() == null || a.getPsychologist().getUser() == null
                || !a.getPsychologist().getUser().getId().equals(psychologistUserId)) {
            throw new ResourceNotFoundException("Appointment not found for this psychologist");
        }
    }

    private Appointment require(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
    }

    private static String fullName(User u) {
        String fn = u.getFirstName() != null ? u.getFirstName() : "";
        String ln = u.getLastName()  != null ? u.getLastName()  : "";
        String name = (fn + " " + ln).trim();
        return name.isEmpty() ? u.getEmail() : name;
    }

    private static String firstNameOrEmail(User u) {
        return u.getFirstName() != null && !u.getFirstName().isBlank() ? u.getFirstName() : u.getEmail();
    }

    private void notifyAssigned(Appointment a) {
        // Patient
        if (a.getPatient() != null && a.getPatient().getUser() != null) {
            User patientUser = a.getPatient().getUser();
            safeSend(() -> emailService.sendAppointmentAssigned(
                patientUser.getEmail(), firstNameOrEmail(patientUser),
                a.getPsychologist() != null ? a.getPsychologist().getName() : null,
                a.getStartAt(), a.getSessionFormat()));
        }
        // Psychologist
        if (a.getPsychologist() != null && a.getPsychologist().getUser() != null) {
            User psyUser = a.getPsychologist().getUser();
            safeSend(() -> emailService.sendAppointmentAssignedPsychologist(
                psyUser.getEmail(), firstNameOrEmail(psyUser),
                a.getPatientName(), a.getStartAt(), a.getNote()));
        }
    }

    private void notifyConfirmed(Appointment a) {
        if (a.getPatient() != null && a.getPatient().getUser() != null) {
            User patientUser = a.getPatient().getUser();
            safeSend(() -> emailService.sendAppointmentConfirmed(
                patientUser.getEmail(), firstNameOrEmail(patientUser),
                a.getPsychologist() != null ? a.getPsychologist().getName() : null,
                a.getStartAt()));
        }
    }

    private void notifyRejected(Appointment a) {
        if (a.getPatient() != null && a.getPatient().getUser() != null) {
            User patientUser = a.getPatient().getUser();
            safeSend(() -> emailService.sendAppointmentRejected(
                patientUser.getEmail(), firstNameOrEmail(patientUser),
                a.getOperatorNote()));
        }
    }

    private void notifyCancelled(Appointment a, String by) {
        if (a.getPatient() != null && a.getPatient().getUser() != null) {
            User patientUser = a.getPatient().getUser();
            safeSend(() -> emailService.sendAppointmentCancelled(
                patientUser.getEmail(), firstNameOrEmail(patientUser),
                a.getPsychologist() != null ? a.getPsychologist().getName() : null,
                a.getStartAt(), by));
        }
        if (a.getPsychologist() != null && a.getPsychologist().getUser() != null) {
            User psyUser = a.getPsychologist().getUser();
            safeSend(() -> emailService.sendAppointmentCancelled(
                psyUser.getEmail(), firstNameOrEmail(psyUser),
                a.getPatientName(), a.getStartAt(), by));
        }
    }

    private void safeSend(Runnable r) {
        try { r.run(); } catch (Exception e) { log.warn("Email notification failed: {}", e.getMessage()); }
    }

    // ─── DTO mapping ─────────────────────────────────────────────────────────

    private AppointmentDto toLegacyDto(Appointment a) {
        return new AppointmentDto(a.getId(), a.getPatientName(), a.getPhone(),
            a.getPsychologistName(), a.getNote(), a.getPreferredDate(), a.getStatus(), a.getCreatedAt());
    }

    private AppointmentDetailDto toDetailDto(Appointment a) {
        Long patientId = a.getPatient() != null ? a.getPatient().getId() : null;
        String patientEmail = (a.getPatient() != null && a.getPatient().getUser() != null)
            ? a.getPatient().getUser().getEmail() : null;
        Long psyId = a.getPsychologist() != null ? a.getPsychologist().getId() : null;
        Long reqPsyId = a.getRequestedPsychologist() != null ? a.getRequestedPsychologist().getId() : null;
        String reqPsyName = a.getRequestedPsychologist() != null ? a.getRequestedPsychologist().getName() : null;
        Long opId = a.getAssignedByOperator() != null ? a.getAssignedByOperator().getId() : null;

        return new AppointmentDetailDto(
            a.getId(), a.getStatus(),
            patientId, a.getPatientName(), patientEmail, a.getPhone(),
            psyId, a.getPsychologistName(),
            reqPsyId, reqPsyName,
            a.getRequestedStartAt(), a.getStartAt(), a.getEndAt(),
            a.getSessionFormat(), a.getNote(), a.getOperatorNote(),
            opId, a.getCreatedAt(), a.getUpdatedAt()
        );
    }

    /** Helper exposed for ReportsService etc. */
    public LocalDateTime nowForTest() { return LocalDateTime.now(); }
}
