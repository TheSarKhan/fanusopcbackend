package com.fanus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_value", nullable = false)
    private int statValue;

    @Column(nullable = false)
    private String suffix;

    @Column(nullable = false)
    private String label;

    @Column(name = "sub_label", nullable = false)
    private String subLabel;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
