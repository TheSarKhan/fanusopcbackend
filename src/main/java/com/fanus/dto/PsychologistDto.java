package com.fanus.dto;

import java.util.List;

public record PsychologistDto(
    Long id,
    String name,
    String title,
    List<String> specializations,
    String experience,
    String sessionsCount,
    String rating,
    String photoUrl,
    String accentColor,
    String bgColor,
    int displayOrder,
    boolean active
) {}
