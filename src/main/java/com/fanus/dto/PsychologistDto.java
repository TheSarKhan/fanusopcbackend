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
    String bio,
    String phone,
    String email,
    String languages,
    String sessionTypes,
    String activityFormat,
    String university,
    String degree,
    String graduationYear,
    String accentColor,
    String bgColor,
    int displayOrder,
    boolean active
) {}
