package com.fanus.dto;

import java.time.LocalTime;

public record TimeSlotDto(
    Long id,
    Long psychologistId,
    int dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    boolean active
) {}
