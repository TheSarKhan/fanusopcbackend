package com.fanus.controller;

import com.fanus.dto.*;
import com.fanus.entity.User;
import com.fanus.repository.UserRepository;
import com.fanus.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final PsychologistService psychologistService;
    private final PsychologistApplicationService psychologistApplicationService;
    private final StatService statService;
    private final AnnouncementService announcementService;
    private final BlogPostService blogPostService;
    private final FaqService faqService;
    private final TestimonialService testimonialService;
    private final SiteConfigService siteConfigService;
    private final AppointmentService appointmentService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final DashboardService dashboardService;
    private final ReportsService reportsService;

    // ─── Operators ────────────────────────────────────────────────────────────
    @PostMapping("/operators")
    public ResponseEntity<Map<String, Object>> createOperator(
            @Valid @RequestBody CreateOperatorRequest req) {

        if (userRepository.existsByEmail(req.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Bu email artıq mövcuddur"));
        }

        String tempPassword = "Fanus@" + UUID.randomUUID().toString().substring(0, 8);

        User operator = User.builder()
            .email(req.email())
            .password(passwordEncoder.encode(tempPassword))
            .role("OPERATOR")
            .firstName(req.firstName())
            .lastName(req.lastName())
            .phone(req.phone())
            .emailVerified(true)
            .build();

        operator = userRepository.save(operator);
        emailService.sendOperatorCredentialsEmail(
            operator.getEmail(), operator.getFirstName(), tempPassword);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "id", operator.getId(),
                "email", operator.getEmail(),
                "message", "Operator hesabı yaradıldı və email göndərildi"
            ));
    }

    // ─── Upload ───────────────────────────────────────────────────────────────
    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.store(file);
        return Map.of("url", url);
    }

    // ─── Psychologists ────────────────────────────────────────────────────────
    @GetMapping("/psychologists")
    public List<PsychologistDto> allPsychologists() { return psychologistService.findAll(); }

    @PostMapping("/psychologists")
    public ResponseEntity<PsychologistDto> createPsychologist(@Valid @RequestBody PsychologistRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(psychologistService.create(req));
    }

    @PutMapping("/psychologists/{id}")
    public PsychologistDto updatePsychologist(@PathVariable Long id, @Valid @RequestBody PsychologistRequest req) {
        return psychologistService.update(id, req);
    }

    @DeleteMapping("/psychologists/{id}")
    public ResponseEntity<Void> deletePsychologist(@PathVariable Long id) {
        psychologistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Stats ────────────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public List<StatDto> allStats() { return statService.findAll(); }

    @PostMapping("/stats")
    public ResponseEntity<StatDto> createStat(@Valid @RequestBody StatRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(statService.create(req));
    }

    @PutMapping("/stats/{id}")
    public StatDto updateStat(@PathVariable Long id, @Valid @RequestBody StatRequest req) {
        return statService.update(id, req);
    }

    @DeleteMapping("/stats/{id}")
    public ResponseEntity<Void> deleteStat(@PathVariable Long id) {
        statService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Announcements ────────────────────────────────────────────────────────
    @GetMapping("/announcements")
    public List<AnnouncementDto> allAnnouncements() { return announcementService.findAll(); }

    @PostMapping("/announcements")
    public ResponseEntity<AnnouncementDto> createAnnouncement(@Valid @RequestBody AnnouncementRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementService.create(req));
    }

    @PutMapping("/announcements/{id}")
    public AnnouncementDto updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementRequest req) {
        return announcementService.update(id, req);
    }

    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Blog Posts ───────────────────────────────────────────────────────────
    @GetMapping("/blog-posts")
    public List<BlogPostDto> allBlogPosts() { return blogPostService.findAll(); }

    @PostMapping("/blog-posts")
    public ResponseEntity<BlogPostDto> createBlogPost(@Valid @RequestBody BlogPostRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostService.create(req));
    }

    @PutMapping("/blog-posts/{id}")
    public BlogPostDto updateBlogPost(@PathVariable Long id, @Valid @RequestBody BlogPostRequest req) {
        return blogPostService.update(id, req);
    }

    @DeleteMapping("/blog-posts/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
        blogPostService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── FAQs ─────────────────────────────────────────────────────────────────
    @GetMapping("/faqs")
    public List<FaqDto> allFaqs() { return faqService.findAll(); }

    @PostMapping("/faqs")
    public ResponseEntity<FaqDto> createFaq(@Valid @RequestBody FaqRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(faqService.create(req));
    }

    @PutMapping("/faqs/{id}")
    public FaqDto updateFaq(@PathVariable Long id, @Valid @RequestBody FaqRequest req) {
        return faqService.update(id, req);
    }

    @DeleteMapping("/faqs/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Testimonials ─────────────────────────────────────────────────────────
    @GetMapping("/testimonials")
    public List<TestimonialDto> allTestimonials() { return testimonialService.findAll(); }

    @PostMapping("/testimonials")
    public ResponseEntity<TestimonialDto> createTestimonial(@Valid @RequestBody TestimonialRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testimonialService.create(req));
    }

    @PutMapping("/testimonials/{id}")
    public TestimonialDto updateTestimonial(@PathVariable Long id, @Valid @RequestBody TestimonialRequest req) {
        return testimonialService.update(id, req);
    }

    @DeleteMapping("/testimonials/{id}")
    public ResponseEntity<Void> deleteTestimonial(@PathVariable Long id) {
        testimonialService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Site Config ──────────────────────────────────────────────────────────
    @GetMapping("/site-config")
    public Map<String, String> getSiteConfig() { return siteConfigService.findAll(); }

    @PutMapping("/site-config")
    public ResponseEntity<Void> updateSiteConfig(@RequestBody Map<String, String> configs) {
        siteConfigService.update(configs);
        return ResponseEntity.ok().build();
    }

    // ─── Appointments ─────────────────────────────────────────────────────────
    @GetMapping("/appointments")
    public List<AppointmentDto> allAppointments() { return appointmentService.findAll(); }

    @PutMapping("/appointments/{id}/status")
    public AppointmentDto updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return appointmentService.updateStatus(id, body.get("status"));
    }

    // ─── Dashboard counts ─────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        long pendingAppointments = appointmentService.findAll().stream()
            .filter(a -> "PENDING".equals(a.status())).count();
        return Map.of(
            "psychologists", psychologistService.findAll().size(),
            "announcements", announcementService.findAll().size(),
            "blogPosts", blogPostService.findAll().size(),
            "faqs", faqService.findAll().size(),
            "testimonials", testimonialService.findAll().size(),
            "appointments", appointmentService.findAll().size(),
            "pendingAppointments", pendingAppointments
        );
    }

    // ─── Rich dashboard metrics ──────────────────────────────────────────────
    @GetMapping("/dashboard/metrics")
    public DashboardDto dashboardMetrics() {
        return dashboardService.build();
    }

    // ─── Reports / analytics ─────────────────────────────────────────────────
    @GetMapping("/reports")
    public ReportsDto reports() {
        return reportsService.build();
    }

    // ─── Psychologist Applications ────────────────────────────────────────────
    @GetMapping("/applications")
    public List<PsychologistApplicationDto> allApplications() {
        return psychologistApplicationService.findAll();
    }

    @PutMapping("/applications/{id}/approve")
    public PsychologistApplicationDto approveApplication(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("adminNote") : null;
        return psychologistApplicationService.approve(id, note);
    }

    @PutMapping("/applications/{id}/reject")
    public PsychologistApplicationDto rejectApplication(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("adminNote") : null;
        return psychologistApplicationService.reject(id, note);
    }

    // ─── Users (lookup for activity feed / settings) ─────────────────────────
    @GetMapping("/users/summary")
    public Map<String, Long> usersSummary() {
        return Map.of(
            "PATIENT", userRepository.countByRole("PATIENT"),
            "PSYCHOLOGIST", userRepository.countByRole("PSYCHOLOGIST"),
            "OPERATOR", userRepository.countByRole("OPERATOR"),
            "ADMIN", userRepository.countByRole("ADMIN")
        );
    }
}
