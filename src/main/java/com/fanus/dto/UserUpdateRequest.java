package com.fanus.dto;

public record UserUpdateRequest(
    String firstName,
    String lastName,
    String phone,
    String role,
    Boolean emailVerified,
    Boolean active
) {}
