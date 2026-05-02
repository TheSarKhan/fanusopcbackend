package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BlogPostRequest(
    @NotBlank String category,
    @NotBlank String categoryColor,
    @NotBlank String categoryBg,
    @NotBlank String title,
    String excerpt,
    String content,
    String coverImageUrl,
    @NotNull LocalDate publishedDate,
    String emoji,
    @NotBlank String slug,
    boolean featured,
    boolean active,
    String status
) {}
