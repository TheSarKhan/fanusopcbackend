package com.fanus.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String email,
    String role,
    Long userId,
    String firstName,
    String lastName
) {
    public AuthResponse(String accessToken, String refreshToken, String email, String role) {
        this(accessToken, refreshToken, email, role, null, null, null);
    }
}
