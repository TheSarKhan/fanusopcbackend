package com.fanus.dto;

public record BlogCategoryDto(
    Long id,
    String name,
    String color,
    String bg,
    String emoji,
    boolean active,
    int sortOrder
) {}
