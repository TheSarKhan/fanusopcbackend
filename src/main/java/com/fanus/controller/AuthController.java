package com.fanus.controller;

import com.fanus.dto.*;
import com.fanus.entity.Patient;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.UserRepository;
import com.fanus.security.JwtTokenProvider;
import com.fanus.security.RefreshTokenService;
import com.fanus.service.EmailService;
import com.fanus.service.PatientService;
import com.fanus.service.PsychologistApplicationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;
    private final EmailService emailService;
    private final PsychologistApplicationService psychologistApplicationService;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    // ─── Login ────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody AuthRequest req,
            HttpServletResponse response) {
         authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = userRepository.findByEmail(req.email())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Hesabınız deaktiv edilmişdir. Ətraflı məlumat üçün administrasiya ilə əlaqə saxlayın."));
        }

        if ("PATIENT".equals(user.getRole()) && !user.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Email ünvanınız hələ təsdiqlənməyib. Emailinizi yoxlayın."));
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String tokenId = refreshTokenService.create(user.getId());
        String refreshToken = user.getId() + ":" + tokenId;

        setAuthCookies(response, accessToken, refreshToken);

        return ResponseEntity.ok(new AuthResponse(
            accessToken, refreshToken,
            user.getEmail(), user.getRole(),
            user.getId(), user.getFirstName(), user.getLastName()
        ));
    }

    // ─── Register Patient ─────────────────────────────────────────────────────

    @PostMapping("/register/patient")
    public ResponseEntity<Map<String, String>> registerPatient(
            @Valid @RequestBody RegisterPatientRequest req) {

        if (userRepository.existsByEmail(req.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Bu email artıq qeydiyyatdan keçib"));
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
            .email(req.email())
            .password(passwordEncoder.encode(req.password()))
            .role("PATIENT")
            .firstName(req.firstName())
            .lastName(req.lastName())
            .phone(req.phone())
            .emailVerified(false)
            .emailVerificationToken(verificationToken)
            .verificationExpiresAt(LocalDateTime.now().plusHours(24))
            .build();

        user = userRepository.save(user);
        patientService.createForUser(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationToken);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Qeydiyyat uğurlu oldu. Email ünvanınızı təsdiqləyin."));
    }

    // ─── Register Psychologist ────────────────────────────────────────────────

    @PostMapping(value = "/register/psychologist", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> registerPsychologist(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam("password") String password,
            @RequestParam(value = "languages", required = false) List<String> languages,
            @RequestParam("university") String university,
            @RequestParam("degree") String degree,
            @RequestParam("graduationYear") String graduationYear,
            @RequestParam(value = "specializations", required = false) List<String> specializations,
            @RequestParam(value = "sessionTypes", required = false) List<String> sessionTypes,
            @RequestParam(value = "experienceYears", required = false) String experienceYears,
            @RequestParam(value = "activityFormat", required = false) String activityFormat,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "certifications", required = false) List<String> certifications,
            @RequestParam("diplomaFile") MultipartFile diplomaFile,
            @RequestParam(value = "certificateFiles", required = false) List<MultipartFile> certificateFiles,
            @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {

        if (diplomaFile == null || diplomaFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Diplom faylı tələb olunur"));
        }

        if (userRepository.existsByEmail(email) || psychologistApplicationService.emailExists(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Bu email artıq qeydiyyatdan keçib"));
        }

        psychologistApplicationService.submit(
            firstName, lastName, email, phone, password,
            university, degree, graduationYear,
            specializations, sessionTypes, experienceYears, bio, certifications,
            diplomaFile, languages, activityFormat, certificateFiles, photoFile
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Müraciətiniz qəbul edildi. Yoxlama tamamlandıqdan sonra sizə bildiriş göndəriləcək."));
    }

    // ─── Verify Email ─────────────────────────────────────────────────────────

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        User user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Yanlış və ya köhnəlmiş link"));

        if (user.getVerificationExpiresAt() != null &&
                user.getVerificationExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("error", "Təsdiq linki müddəti bitib"));
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setVerificationExpiresAt(null);
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        return ResponseEntity.ok(Map.of("message", "Email uğurla təsdiqləndi"));
    }

    // ─── Forgot Password ──────────────────────────────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {

        userRepository.findByEmail(req.email()).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetExpiresAt(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
            String firstName = user.getFirstName() != null ? user.getFirstName() : "İstifadəçi";
            emailService.sendPasswordResetEmail(user.getEmail(), firstName, resetToken);
        });

        return ResponseEntity.ok(Map.of("message",
            "Əgər bu email sistemdə mövcuddursa, şifrə sıfırlama linki göndərildi"));
    }

    // ─── Reset Password ───────────────────────────────────────────────────────

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {

        User user = userRepository.findByPasswordResetToken(req.token())
            .orElseThrow(() -> new ResourceNotFoundException("Yanlış və ya köhnəlmiş link"));

        if (user.getPasswordResetExpiresAt() != null &&
                user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("error", "Şifrə sıfırlama linkinin müddəti bitib"));
        }

        user.setPassword(passwordEncoder.encode(req.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Şifrəniz uğurla yeniləndi"));
    }

    // ─── Refresh ─────────────────────────────────────────────────────────────

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @Valid @RequestBody RefreshRequest req,
            HttpServletResponse response) {

        String[] parts = req.refreshToken().split(":");
        if (parts.length != 2)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));

        Long userId;
        try { userId = Long.parseLong(parts[0]); } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
        String tokenId = parts[1];

        if (!refreshTokenService.validate(userId, tokenId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Refresh token expired or revoked"));
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Hesab deaktivdir"));
        }

        String newAccess = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        refreshTokenService.revoke(userId, tokenId);
        String newRefresh = userId + ":" + refreshTokenService.create(userId);

        setAuthCookies(response, newAccess, newRefresh);

        return ResponseEntity.ok(new AuthResponse(
            newAccess, newRefresh, user.getEmail(), user.getRole(),
            user.getId(), user.getFirstName(), user.getLastName()
        ));
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody(required = false) RefreshRequest req,
            HttpServletResponse response) {

        if (req != null) {
            String[] parts = req.refreshToken().split(":");
            if (parts.length == 2) {
                try {
                    Long userId = Long.parseLong(parts[0]);
                    refreshTokenService.revoke(userId, parts[1]);
                } catch (NumberFormatException ignored) {}
            }
        }

        clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }

    // ─── Cookie helpers ───────────────────────────────────────────────────────

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader("Set-Cookie", buildCookie("accessToken", accessToken,
            (int) (tokenProvider.getAccessTokenExpiryMs() / 1000)).toString());
        response.addHeader("Set-Cookie", buildCookie("refreshToken", refreshToken,
            7 * 24 * 3600).toString());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        response.addHeader("Set-Cookie", buildCookie("accessToken", "", 0).toString());
        response.addHeader("Set-Cookie", buildCookie("refreshToken", "", 0).toString());
    }

    private ResponseCookie buildCookie(String name, String value, int maxAge) {
        String sameSite = "Lax";
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .sameSite(sameSite)
            .maxAge(maxAge);
        if (!cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }
        return builder.build();
    }
}
