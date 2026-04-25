package com.fanus.service;

import com.fanus.dto.PsychologistDto;
import com.fanus.dto.PsychologistRequest;
import com.fanus.entity.Psychologist;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.PsychologistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PsychologistService {

    private final PsychologistRepository repo;

    public List<PsychologistDto> findAll() {
        return repo.findAllByOrderByIdAsc().stream().map(this::toDto).toList();
    }

    public List<PsychologistDto> findActive() {
        return repo.findByActiveTrueOrderByDisplayOrderAsc().stream().map(this::toDto).toList();
    }

    public PsychologistDto findById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Psychologist not found: " + id)));
    }

    @Transactional
    public PsychologistDto create(PsychologistRequest req) {
        Psychologist p = Psychologist.builder()
            .name(req.name()).title(req.title())
            .specializations(req.specializations())
            .experience(req.experience()).sessionsCount(req.sessionsCount())
            .rating(req.rating()).photoUrl(req.photoUrl())
            .accentColor(req.accentColor()).bgColor(req.bgColor())
            .displayOrder(req.displayOrder()).active(req.active())
            .build();
        return toDto(repo.save(p));
    }

    @Transactional
    public PsychologistDto update(Long id, PsychologistRequest req) {
        Psychologist p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Psychologist not found: " + id));
        p.setName(req.name()); p.setTitle(req.title());
        p.setSpecializations(req.specializations());
        p.setExperience(req.experience()); p.setSessionsCount(req.sessionsCount());
        p.setRating(req.rating()); p.setPhotoUrl(req.photoUrl());
        p.setAccentColor(req.accentColor()); p.setBgColor(req.bgColor());
        p.setDisplayOrder(req.displayOrder()); p.setActive(req.active());
        return toDto(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Psychologist not found: " + id);
        repo.deleteById(id);
    }

    private PsychologistDto toDto(Psychologist p) {
        return new PsychologistDto(p.getId(), p.getName(), p.getTitle(), p.getSpecializations(),
            p.getExperience(), p.getSessionsCount(), p.getRating(), p.getPhotoUrl(),
            p.getAccentColor(), p.getBgColor(), p.getDisplayOrder(), p.isActive());
    }
}
