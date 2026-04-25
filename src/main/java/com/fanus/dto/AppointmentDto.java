package com.fanus.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentDto(
    Long id,
    String patientName,
    String phone,
    String psychologistName,
    String note,
    LocalDate preferredDate,
    String status,
    LocalDateTime createdAt
) {}
