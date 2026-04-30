package com.fanus.dto;

import java.util.List;
import java.util.Map;

public record PagedUsersResponse(
    List<UserDto> content,
    long totalElements,
    int totalPages,
    int page,
    int size,
    Map<String, Long> roleCounts  // stat cards: PATIENT, PSYCHOLOGIST, OPERATOR, ADMIN, total
) {}
