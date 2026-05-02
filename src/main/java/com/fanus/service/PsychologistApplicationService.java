package com.fanus.service;

import com.fanus.dto.PsychologistApplicationDto;
import com.fanus.entity.Psychologist;
import com.fanus.entity.PsychologistApplication;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.PsychologistApplicationRepository;
import com.fanus.repository.PsychologistRepository;
import com.fanus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PsychologistApplicationService {

    private final PsychologistApplicationRepository repository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PsychologistRepository psychologistRepository;

    @Value("${app.email.admin}")
    private String adminEmail;

    private static final String[][] COLOR_PALETTE = {
        {"#4A9B7F", "#E8F5F0"},
        {"#5A4FC8", "#EEECFB"},
        {"#2e86c1", "#E3F0FB"},
        {"#c0392b", "#FDECEA"},
        {"#d35400", "#FEF0E7"},
        {"#1a5276", "#E8EEF6"},
    };

    public List<PsychologistApplicationDto> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    public PsychologistApplicationDto findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found for email: " + email));
    }

    @Transactional
    public PsychologistApplicationDto approve(Long id, String adminNote) {
        PsychologistApplication app = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!"PENDING".equals(app.getStatus())) {
            throw new IllegalStateException("Application is not in PENDING state");
        }

        User user = User.builder()
            .email(app.getEmail())
            .password(app.getPasswordHash())
            .role("PSYCHOLOGIST")
            .firstName(app.getFirstName())
            .lastName(app.getLastName())
            .phone(app.getPhone())
            .emailVerified(true)
            .build();
        userRepository.save(user);

        int psyCount = (int) psychologistRepository.count();
        String[] colors = COLOR_PALETTE[psyCount % COLOR_PALETTE.length];
        String experience = (app.getExperienceYears() != null && !app.getExperienceYears().isBlank())
            ? (app.getExperienceYears().contains("il") ? app.getExperienceYears() : app.getExperienceYears() + " il")
            : "0 il";

        Psychologist psychologist = Psychologist.builder()
            .name(app.getFirstName() + " " + app.getLastName())
            .title("Psixoloq")
            .specializations(parseCommaList(app.getSpecializations()))
            .experience(experience)
            .sessionsCount("0")
            .rating("5.0")
            .photoUrl(app.getPhotoUrl())
            .bio(app.getBio())
            .phone(app.getPhone())
            .email(app.getEmail())
            .languages(app.getLanguages())
            .sessionTypes(app.getSessionTypes())
            .activityFormat(app.getActivityFormat())
            .university(app.getUniversity())
            .degree(app.getDegree())
            .graduationYear(app.getGraduationYear())
            .accentColor(colors[0])
            .bgColor(colors[1])
            .displayOrder(psyCount + 1)
            .active(true)
            .build();
        psychologistRepository.save(psychologist);

        app.setStatus("APPROVED");
        app.setAdminNote(adminNote);
        app.setReviewedAt(LocalDateTime.now());
        app = repository.save(app);

        try {
            emailService.sendPsychologistApproved(app.getEmail(), app.getFirstName());
        } catch (Exception e) {
            log.error("Failed to send approval email to {}: {}", app.getEmail(), e.getMessage());
        }

        return toDto(app);
    }

    @Transactional
    public PsychologistApplicationDto reject(Long id, String adminNote) {
        PsychologistApplication app = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!"PENDING".equals(app.getStatus())) {
            throw new IllegalStateException("Application is not in PENDING state");
        }

        app.setStatus("REJECTED");
        app.setAdminNote(adminNote);
        app.setReviewedAt(LocalDateTime.now());
        app = repository.save(app);

        try {
            emailService.sendPsychologistRejected(app.getEmail(), app.getFirstName(), adminNote);
        } catch (Exception e) {
            log.error("Failed to send rejection email to {}: {}", app.getEmail(), e.getMessage());
        }

        return toDto(app);
    }

    private PsychologistApplicationDto toDto(PsychologistApplication a) {
        return new PsychologistApplicationDto(
            a.getId(), a.getFirstName(), a.getLastName(), a.getEmail(), a.getPhone(),
            a.getUniversity(), a.getDegree(), a.getGraduationYear(),
            a.getSpecializations(), a.getSessionTypes(), a.getExperienceYears(),
            a.getBio(), a.getCertifications(), a.getLanguages(), a.getActivityFormat(),
            a.getDiplomaFileUrl(), a.getCertificateFileUrls(),
            a.getStatus(), a.getAdminNote(), a.getCreatedAt(), a.getReviewedAt(),
            a.getPhotoUrl()
        );
    }

    private static List<String> parseCommaList(String value) {
        if (value == null || value.isBlank()) return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String s : value.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) result.add(s);
        }
        return result;
    }

    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    public PsychologistApplication submit(
            String firstName,
            String lastName,
            String email,
            String phone,
            String password,
            String university,
            String degree,
            String graduationYear,
            List<String> specializations,
            List<String> sessionTypes,
            String experienceYears,
            String bio,
            List<String> certifications,
            MultipartFile diplomaFile,
            List<String> languages,
            String activityFormat,
            List<MultipartFile> certificateFiles,
            MultipartFile photoFile
    ) {
        String diplomaFileUrl = null;
        if (diplomaFile != null && !diplomaFile.isEmpty()) {
            diplomaFileUrl = fileStorageService.store(diplomaFile);
        }

        List<String> certUrls = new ArrayList<>();
        if (certificateFiles != null) {
            for (MultipartFile f : certificateFiles) {
                if (f != null && !f.isEmpty()) {
                    certUrls.add(fileStorageService.store(f));
                }
            }
        }

        String photoUrl = null;
        if (photoFile != null && !photoFile.isEmpty()) {
            photoUrl = fileStorageService.store(photoFile);
        }

        PsychologistApplication app = PsychologistApplication.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .phone(phone)
            .passwordHash(passwordEncoder.encode(password))
            .university(university)
            .degree(degree)
            .graduationYear(graduationYear)
            .diplomaFileUrl(diplomaFileUrl)
            .specializations(specializations != null ? String.join(",", specializations) : null)
            .sessionTypes(sessionTypes != null ? String.join(",", sessionTypes) : null)
            .experienceYears(experienceYears)
            .bio(bio)
            .certifications(certifications != null ? String.join(",", certifications) : null)
            .languages(languages != null ? String.join(",", languages) : null)
            .activityFormat(activityFormat)
            .certificateFileUrls(!certUrls.isEmpty() ? String.join(",", certUrls) : null)
            .photoUrl(photoUrl)
            .status("PENDING")
            .build();

        app = repository.save(app);

        try {
            emailService.sendPsychologistApplicationReceived(email, firstName);
            emailService.sendPsychologistApplicationAdminNotification(adminEmail, firstName, lastName, email);
        } catch (Exception e) {
            log.error("Failed to send application emails for {}: {}", email, e.getMessage());
        }

        return app;
    }
}
