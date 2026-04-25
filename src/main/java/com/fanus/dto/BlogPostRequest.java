package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BlogPostRequest(
    @NotBlank String category,
    @NotBlank String categoryColor,
    @NotBlank String categoryBg,
    @NotBlank String title,
    @NotBlank String excerpt,
    int readTimeMinutes,
    @NotNull LocalDate publishedDate,
    @NotBlank String emoji,
    @NotBlank String slug,
    boolean featured,
    boolean active
) {}
