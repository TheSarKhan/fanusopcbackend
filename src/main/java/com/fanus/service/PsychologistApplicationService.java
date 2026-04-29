package com.fanus.service;

import com.fanus.entity.PsychologistApplication;
import com.fanus.repository.PsychologistApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${app.email.admin}")
    private String adminEmail;

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
            List<MultipartFile> certificateFiles
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
