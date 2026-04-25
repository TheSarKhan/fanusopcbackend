package com.fanus.dto;

import java.time.LocalDate;

public record BlogPostDto(
    Long id,
    String category,
    String categoryColor,
    String categoryBg,
    String title,
    String excerpt,
    int readTimeMinutes,
    LocalDate publishedDate,
    String emoji,
    String slug,
    boolean featured,
    boolean active
) {}
