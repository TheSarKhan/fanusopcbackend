package com.fanus.service;

import com.fanus.entity.SiteConfig;
import com.fanus.repository.SiteConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteConfigService {

    private final SiteConfigRepository repo;

    public Map<String, String> findAll() {
        return repo.findAll().stream()
            .collect(Collectors.toMap(SiteConfig::getConfigKey, SiteConfig::getConfigValue));
    }

    @Transactional
    public void update(Map<String, String> configs) {
        configs.forEach((key, value) -> {
            SiteConfig cfg = repo.findById(key).orElse(new SiteConfig(key, value));
            cfg.setConfigValue(value);
            repo.save(cfg);
        });
    }
}
