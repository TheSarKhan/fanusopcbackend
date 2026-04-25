package com.fanus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "site_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SiteConfig {

    @Id
    @Column(name = "config_key")
    private String configKey;

    @Column(name = "config_value", nullable = false, columnDefinition = "TEXT")
    private String configValue;
}
