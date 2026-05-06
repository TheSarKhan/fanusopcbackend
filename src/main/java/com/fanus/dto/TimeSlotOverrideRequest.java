package com.fanus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotOverrideRequest(
    @NotNull LocalDate overrideDate,
    @Pattern(regexp = "BLOCK|EXTRA") String overrideType,
    LocalTime startTime,
    LocalTime endTime,
    String note
) {}
