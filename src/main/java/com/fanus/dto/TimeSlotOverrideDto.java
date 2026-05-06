package com.fanus.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotOverrideDto(
    Long id,
    Long psychologistId,
    LocalDate overrideDate,
    String overrideType,
    LocalTime startTime,
    LocalTime endTime,
    String note
) {}
