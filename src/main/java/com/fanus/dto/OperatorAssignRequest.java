package com.fanus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record OperatorAssignRequest(
    @NotNull Long psychologistId,
    @NotNull LocalDateTime startAt,
    @NotNull LocalDateTime endAt,
    @Pattern(regexp = "ONLINE|IN_PERSON") String sessionFormat,
    String operatorNote
) {}
