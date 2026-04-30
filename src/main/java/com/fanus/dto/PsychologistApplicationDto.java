package com.fanus.dto;

import java.time.LocalDateTime;

public record PsychologistApplicationDto(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String university,
    String degree,
    String graduationYear,
    String specializations,
    String sessionTypes,
    String experienceYears,
    String bio,
    String certifications,
    String languages,
    String activityFormat,
    String diplomaFileUrl,
    String certificateFileUrls,
    String status,
    String adminNote,
    LocalDateTime createdAt,
    LocalDateTime reviewedAt
) {}
