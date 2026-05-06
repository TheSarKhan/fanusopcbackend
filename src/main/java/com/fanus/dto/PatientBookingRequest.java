package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * Body sent by a logged-in patient to request an appointment.
 * Patient may pick a specific psychologist + slot, or leave the choice to the operator.
 */
public record PatientBookingRequest(
    @NotBlank String note,
    Long requestedPsychologistId,
    LocalDateTime requestedStartAt,
    @Pattern(regexp = "ONLINE|IN_PERSON") String sessionFormat
) {}
