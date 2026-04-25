package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;

public record TestimonialRequest(
    @NotBlank String quote,
    @NotBlank String authorName,
    @NotBlank String authorRole,
    @NotBlank String initials,
    @NotBlank String gradient,
    int rating,
    boolean active
) {}
