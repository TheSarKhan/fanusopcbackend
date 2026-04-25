package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AnnouncementRequest(
    @NotBlank String category,
    @NotBlank String categoryColor,
    @NotBlank String categoryBg,
    @NotBlank String title,
    @NotBlank String excerpt,
    @NotNull LocalDate publishedDate,
    @NotBlank String iconType,
    boolean active
) {}
