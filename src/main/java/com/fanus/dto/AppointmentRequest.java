package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record AppointmentRequest(
    @NotBlank String patientName,
    @NotBlank String phone,
    String psychologistName,
    String note,
    LocalDate preferredDate
) {}
