package com.fanus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "psychologist_applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PsychologistApplication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String university;

    @Column(nullable = false)
    private String degree;

    @Column(name = "graduation_year", nullable = false)
    private String graduationYear;

    @Column(name = "diploma_number")
    private String diplomaNumber;

    @Column(name = "diploma_file_url")
    private String diplomaFileUrl;

    @Column(columnDefinition = "TEXT")
    private String specializations;

    @Column(name = "session_types", columnDefinition = "TEXT")
    private String sessionTypes;

    @Column(name = "experience_years")
    private String experienceYears;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(columnDefinition = "TEXT")
    private String languages;

    @Column(name = "activity_format")
    private String activityFormat;

    @Column(name = "certificate_file_urls", columnDefinition = "TEXT")
    private String certificateFileUrls;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
