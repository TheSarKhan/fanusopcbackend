package com.fanus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Legacy/anonymous fields (nullable now) ──────────────────────────────
    @Column(name = "patient_name")
    private String patientName;

    private String phone;

    @Column(name = "psychologist_name")
    private String psychologistName;

    // ─── Relational fields (set when patient is logged in / operator assigns) ─
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psychologist_id")
    private Psychologist psychologist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_psychologist_id")
    private Psychologist requestedPsychologist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_operator_id")
    private User assignedByOperator;

    @Column(name = "requested_start_at")
    private LocalDateTime requestedStartAt;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "session_format", length = 20)
    private String sessionFormat;

    // ─── Notes & state ───────────────────────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "operator_note", columnDefinition = "TEXT")
    private String operatorNote;

    @Column(name = "preferred_date")
    private LocalDate preferredDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
