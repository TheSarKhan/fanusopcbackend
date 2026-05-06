package com.fanus.dto;

import java.time.LocalDateTime;

/** A bookable concrete time window inside a psychologist's availability. */
public record AvailableSlotDto(
    LocalDateTime startAt,
    LocalDateTime endAt
) {}
