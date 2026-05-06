package com.fanus.dto;

import java.time.LocalDateTime;

/** Rich appointment view used by patient/operator/psychologist panels. */
public record AppointmentDetailDto(
    Long id,
    String status,
    // Patient (linked or anonymous)
    Long patientId,
    String patientName,
    String patientEmail,
    String patientPhone,
    // Psychologist (assigned)
    Long psychologistId,
    String psychologistName,
    // Psychologist (originally requested by patient)
    Long requestedPsychologistId,
    String requestedPsychologistName,
    // Time
    LocalDateTime requestedStartAt,
    LocalDateTime startAt,
    LocalDateTime endAt,
    // Misc
    String sessionFormat,
    String note,
    String operatorNote,
    Long assignedByOperatorId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
