package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;

public record BlogCategoryRequest(
    @NotBlank String name,
    @NotBlank String color,
    @NotBlank String bg,
    String emoji,
    boolean active,
    int sortOrder
) {}
