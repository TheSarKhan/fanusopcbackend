package com.fanus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(name = "category_color", nullable = false)
    private String categoryColor;

    @Column(name = "category_bg", nullable = false)
    private String categoryBg;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "read_time_minutes", nullable = false)
    private int readTimeMinutes;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;

    @Column(nullable = false)
    private String emoji;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private boolean featured;

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
