package com.fanus.dto;

import java.time.LocalDate;

public record AnnouncementDto(
    Long id,
    String category,
    String categoryColor,
    String categoryBg,
    String title,
    String excerpt,
    LocalDate publishedDate,
    String iconType,
    boolean active
) {}
