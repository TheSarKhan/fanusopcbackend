package com.fanus.service;

import com.fanus.dto.PsychologistDto;
import com.fanus.dto.PsychologistRequest;
import com.fanus.entity.Psychologist;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.PsychologistRepository;
import com.fanus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PsychologistService {

    private final PsychologistRepository repo;
    private final UserRepository userRepository;

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
            .bio(req.bio()).phone(req.phone()).email(req.email())
            .languages(req.languages()).sessionTypes(req.sessionTypes())
            .activityFormat(req.activityFormat())
            .university(req.university()).degree(req.degree()).graduationYear(req.graduationYear())
            .accentColor(req.accentColor()).bgColor(req.bgColor())
            .displayOrder(req.displayOrder()).active(req.active())
            .build();
        linkUserByEmail(p);
        return toDto(repo.save(p));
    }

    /** Try to link the psychologist profile to an existing PSYCHOLOGIST user with the same email. */
    private void linkUserByEmail(Psychologist p) {
        if (p.getUser() != null || p.getEmail() == null || p.getEmail().isBlank()) return;
        userRepository.findByEmail(p.getEmail())
            .filter(u -> "PSYCHOLOGIST".equals(u.getRole()))
            .ifPresent(p::setUser);
    }

    /** Look up the psychologist profile by linked user. */
    public Psychologist requireByUserId(Long userId) {
        return repo.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Psychologist profile not found for user " + userId));
    }

    @Transactional
    public PsychologistDto update(Long id, PsychologistRequest req) {
        Psychologist p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Psychologist not found: " + id));
        p.setName(req.name()); p.setTitle(req.title());
        p.setSpecializations(req.specializations());
        p.setExperience(req.experience()); p.setSessionsCount(req.sessionsCount());
        p.setRating(req.rating()); p.setPhotoUrl(req.photoUrl());
        p.setBio(req.bio()); p.setPhone(req.phone()); p.setEmail(req.email());
        p.setLanguages(req.languages()); p.setSessionTypes(req.sessionTypes());
        p.setActivityFormat(req.activityFormat());
        p.setUniversity(req.university()); p.setDegree(req.degree()); p.setGraduationYear(req.graduationYear());
        p.setAccentColor(req.accentColor()); p.setBgColor(req.bgColor());
        p.setDisplayOrder(req.displayOrder()); p.setActive(req.active());
        linkUserByEmail(p);
        return toDto(repo.save(p));
    }

    @Transactional
    public PsychologistDto updateSessionMinutes(Long id, int minutes) {
        if (minutes < 15 || minutes > 240) {
            throw new IllegalArgumentException("Sessiya müddəti 15–240 dəqiqə aralığında olmalıdır");
        }
        Psychologist p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Psychologist not found: " + id));
        p.setDefaultSessionMinutes(minutes);
        return toDto(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Psychologist not found: " + id);
        repo.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    private PsychologistDto toDto(Psychologist p) {
        Long userId = p.getUser() != null ? p.getUser().getId() : null;
        return new PsychologistDto(p.getId(), p.getName(), p.getTitle(), p.getSpecializations(),
            p.getExperience(), p.getSessionsCount(), p.getRating(), p.getPhotoUrl(),
            p.getBio(), p.getPhone(), p.getEmail(),
            p.getLanguages(), p.getSessionTypes(), p.getActivityFormat(),
            p.getUniversity(), p.getDegree(), p.getGraduationYear(),
            p.getAccentColor(), p.getBgColor(), p.getDisplayOrder(), p.isActive(),
            p.getDefaultSessionMinutes(), userId);
    }
}
