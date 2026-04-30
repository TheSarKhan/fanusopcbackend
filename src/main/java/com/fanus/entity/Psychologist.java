package com.fanus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "psychologists")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Psychologist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "psychologist_specializations",
        joinColumns = @JoinColumn(name = "psychologist_id")
    )
    @Column(name = "specialization")
    @Builder.Default
    private List<String> specializations = new ArrayList<>();

    @Column(nullable = false)
    private String experience;

    @Column(name = "sessions_count", nullable = false)
    private String sessionsCount;

    @Column(nullable = false)
    private String rating;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String languages;

    @Column(name = "session_types", columnDefinition = "TEXT")
    private String sessionTypes;

    @Column(name = "activity_format")
    private String activityFormat;

    private String university;

    private String degree;

    @Column(name = "graduation_year")
    private String graduationYear;

    @Column(name = "accent_color", nullable = false)
    private String accentColor;

    @Column(name = "bg_color", nullable = false)
    private String bgColor;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (!active) active = true;
    }
}
