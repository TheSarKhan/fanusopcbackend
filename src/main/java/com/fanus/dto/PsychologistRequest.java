package com.fanus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PsychologistRequest(
    @NotBlank String name,
    @NotBlank String title,
    @NotNull List<String> specializations,
    @NotBlank String experience,
    @NotBlank String sessionsCount,
    @NotBlank String rating,
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
    @NotBlank String accentColor,
    @NotBlank String bgColor,
    int displayOrder,
    boolean active
) {}
