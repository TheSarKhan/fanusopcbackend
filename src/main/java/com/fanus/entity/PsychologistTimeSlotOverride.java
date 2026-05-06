package com.fanus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "psychologist_time_slot_overrides")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PsychologistTimeSlotOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "psychologist_id", nullable = false)
    private Psychologist psychologist;

    @Column(name = "override_date", nullable = false)
    private LocalDate overrideDate;

    /** BLOCK = unavailable that day; EXTRA = additional slot just for this date */
    @Column(name = "override_type", nullable = false, length = 20)
    private String overrideType;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(length = 255)
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
