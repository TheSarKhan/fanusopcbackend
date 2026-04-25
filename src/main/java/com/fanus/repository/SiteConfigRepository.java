package com.fanus.repository;

import com.fanus.entity.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteConfigRepository extends JpaRepository<SiteConfig, String> {
}
