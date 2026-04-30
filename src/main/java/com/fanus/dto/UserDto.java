package com.fanus.dto;

import java.time.LocalDateTime;

public record UserDto(
    Long id,
    String email,
    String role,
    String firstName,
    String lastName,
    String phone,
    boolean emailVerified,
    boolean inPsychologistList,
    boolean active,
    LocalDateTime lastLogin,
    LocalDateTime createdAt
) {}
