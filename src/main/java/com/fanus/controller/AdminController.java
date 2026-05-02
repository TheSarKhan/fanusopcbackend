package com.fanus.controller;

import com.fanus.dto.*;
import com.fanus.service.BlogCategoryService;
import com.fanus.entity.ArticleAttachment;
import com.fanus.entity.BlogPost;
import com.fanus.entity.Psychologist;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.ArticleAttachmentRepository;
import com.fanus.repository.BlogPostRepository;
import com.fanus.repository.UserRepository;
import com.fanus.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final PsychologistService psychologistService;
    private final com.fanus.repository.PsychologistRepository psychologistRepository;
    private final com.fanus.repository.PsychologistApplicationRepository psychologistApplicationRepository;
    private final PsychologistApplicationService psychologistApplicationService;
    private final StatService statService;
    private final AnnouncementService announcementService;
    private final BlogPostService blogPostService;
    private final BlogCategoryService blogCategoryService;
    private final BlogPostRepository blogPostRepository;
    private final ArticleAttachmentRepository articleAttachmentRepository;
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

    // ─── Blog Categories ─────────────────────────────────────────────────────
    @GetMapping("/blog-categories")
    public List<BlogCategoryDto> allCategories() { return blogCategoryService.findAll(); }

    @PostMapping("/blog-categories")
    public ResponseEntity<BlogCategoryDto> createCategory(@Valid @RequestBody BlogCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogCategoryService.create(req));
    }

    @PutMapping("/blog-categories/{id}")
    public BlogCategoryDto updateCategory(@PathVariable Long id, @Valid @RequestBody BlogCategoryRequest req) {
        return blogCategoryService.update(id, req);
    }

    @DeleteMapping("/blog-categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        blogCategoryService.delete(id);
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

    @PostMapping("/blog-posts/{id}/attachments")
    public ResponseEntity<ArticleAttachmentDto> addAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "displayOrder", defaultValue = "0") int displayOrder) {
        BlogPost post = blogPostRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id));
        String url = fileStorageService.store(file);
        String fileType = detectFileType(file.getContentType());
        ArticleAttachment att = ArticleAttachment.builder()
            .article(post)
            .fileUrl(url)
            .fileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file")
            .fileType(fileType)
            .fileSizeBytes(file.getSize())
            .displayOrder(displayOrder)
            .build();
        att = articleAttachmentRepository.save(att);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ArticleAttachmentDto(att.getId(), att.getFileUrl(), att.getFileName(),
                att.getFileType(), att.getFileSizeBytes(), att.getDisplayOrder()));
    }

    @DeleteMapping("/blog-posts/{id}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long id,
            @PathVariable Long attachmentId) {
        articleAttachmentRepository.deleteByArticleIdAndId(id, attachmentId);
        return ResponseEntity.noContent().build();
    }

    private String detectFileType(String contentType) {
        if (contentType == null) return "DOCUMENT";
        if (contentType.startsWith("image/")) return "IMAGE";
        if (contentType.startsWith("video/")) return "VIDEO";
        return "DOCUMENT";
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

    // ─── Users ────────────────────────────────────────────────────────────────
    @GetMapping("/users/summary")
    public Map<String, Long> usersSummary() {
        return Map.of(
            "PATIENT", userRepository.countByRole("PATIENT"),
            "PSYCHOLOGIST", userRepository.countByRole("PSYCHOLOGIST"),
            "OPERATOR", userRepository.countByRole("OPERATOR"),
            "ADMIN", userRepository.countByRole("ADMIN")
        );
    }

    @GetMapping("/users")
    public PagedUsersResponse listUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "20")  int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir) {

        // Clamp page size to reasonable bounds
        int safeSize = Math.min(Math.max(size, 5), 100);
        String search = (q != null && !q.isBlank()) ? q.trim().toLowerCase() : null;

        // Build sort
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = List.of("email", "firstName", "lastName", "createdAt", "lastLogin", "role")
            .contains(sort) ? sort : "createdAt";
        Pageable pageable = PageRequest.of(page, safeSize, Sort.by(direction, sortField));

        Page<User> userPage;
        if (role != null && !role.isBlank()) {
            userPage = userRepository.findByRoleFiltered(role, search, pageable);
        } else {
            userPage = userRepository.findAllFiltered(search, pageable);
        }

        List<UserDto> content = userPage.getContent().stream().map(this::toUserDto).toList();

        // Role counts for stat cards (always full dataset, not filtered)
        Map<String, Long> roleCounts = Map.of(
            "total",         userRepository.count(),
            "PATIENT",       userRepository.countByRole("PATIENT"),
            "PSYCHOLOGIST",  userRepository.countByRole("PSYCHOLOGIST"),
            "OPERATOR",      userRepository.countByRole("OPERATOR"),
            "ADMIN",         userRepository.countByRole("ADMIN")
        );

        return new PagedUsersResponse(content, userPage.getTotalElements(),
            userPage.getTotalPages(), page, safeSize, roleCounts);
    }

    @GetMapping("/users/export")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String q) throws IOException {
            
        String search = (q != null && !q.isBlank()) ? q.trim().toLowerCase() : null;
        List<User> users;
        // Fetch without pagination for export (up to a reasonable limit, Pageable.unpaged() works)
        if (role != null && !role.isBlank()) {
            users = userRepository.findByRoleFiltered(role, search, Pageable.unpaged()).getContent();
        } else {
            users = userRepository.findAllFiltered(search, Pageable.unpaged()).getContent();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("İstifadəçilər");
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Email", "Ad", "Soyad", "Telefon", "Rol", "Status", "Qeydiyyat"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (User u : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u.getId());
                row.createCell(1).setCellValue(u.getEmail());
                row.createCell(2).setCellValue(u.getFirstName() == null ? "" : u.getFirstName());
                row.createCell(3).setCellValue(u.getLastName() == null ? "" : u.getLastName());
                row.createCell(4).setCellValue(u.getPhone() == null ? "" : u.getPhone());
                row.createCell(5).setCellValue(u.getRole());
                row.createCell(6).setCellValue(u.isActive() ? "Aktiv" : "Deaktiv");
                row.createCell(7).setCellValue(u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "istifadeciler.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);
        }
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return toUserDto(userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @PutMapping("/users/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.firstName() != null) u.setFirstName(req.firstName());
        if (req.lastName()  != null) u.setLastName(req.lastName());
        if (req.phone()     != null) u.setPhone(req.phone());
        if (req.role()      != null) u.setRole(req.role());
        if (req.emailVerified() != null) u.setEmailVerified(req.emailVerified());
        if (req.active()    != null) u.setActive(req.active());
        return toUserDto(userRepository.save(u));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Also delete associated psychologist profile if exists
        psychologistRepository.findByEmail(u.getEmail()).ifPresent(p -> {
            psychologistRepository.delete(p);
        });

        userRepository.delete(u);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/toggle-active")
    public UserDto toggleActive(@PathVariable Long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setActive(!u.isActive());
        User saved = userRepository.save(u);

        // Also toggle psychologist profile if exists
        psychologistRepository.findByEmail(u.getEmail()).ifPresent(p -> {
            p.setActive(saved.isActive());
            psychologistRepository.save(p);
        });

        return toUserDto(saved);
    }

    @GetMapping("/users/{id}/psychologist-profile")
    public ResponseEntity<PsychologistDto> getUserPsychologistProfile(@PathVariable Long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return psychologistRepository.findByEmail(u.getEmail())
            .map(p -> ResponseEntity.ok(psychologistService.findById(p.getId())))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/users/{id}/application")
    public ResponseEntity<PsychologistApplicationDto> getUserApplication(@PathVariable Long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        try {
            return ResponseEntity.ok(psychologistApplicationService.findByEmail(u.getEmail()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/{id}/psychologist-profile")
    public ResponseEntity<PsychologistDto> updateUserPsychologistProfile(
            @PathVariable Long id,
            @RequestBody PsychologistRequest req) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        return psychologistRepository.findByEmail(u.getEmail())
            .map(p -> ResponseEntity.ok(psychologistService.update(p.getId(), req)))
            .orElseGet(() -> ResponseEntity.ok(psychologistService.create(req)));
    }

    @PostMapping("/users/{id}/add-to-psychologists")
    public ResponseEntity<Map<String, Object>> addToPsychologists(@PathVariable Long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"PSYCHOLOGIST".equals(u.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "İstifadəçi psixoloq deyil"));
        }
        if (psychologistRepository.existsByEmail(u.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Bu istifadəçi artıq psixoloqlar siyahısındadır"));
        }

        String name = ((u.getFirstName() != null ? u.getFirstName() : "") + " "
                     + (u.getLastName()  != null ? u.getLastName()  : "")).trim();
        if (name.isEmpty()) name = u.getEmail();

        com.fanus.entity.PsychologistApplication app = psychologistApplicationRepository.findByEmail(u.getEmail()).orElse(null);
        
        com.fanus.dto.PsychologistRequest req;
        if (app != null) {
            java.util.List<String> specs = new java.util.ArrayList<>();
            if (app.getSpecializations() != null) {
                for (String s : app.getSpecializations().split(",")) {
                    if (!s.trim().isEmpty()) specs.add(s.trim());
                }
            }
            
            req = new com.fanus.dto.PsychologistRequest(
                name, "Psixoloq", specs, 
                app.getExperienceYears() != null ? app.getExperienceYears() + " il" : "—", 
                "0", "0.0",
                app.getPhotoUrl(), app.getBio(), u.getPhone(), u.getEmail(),
                app.getLanguages(), app.getSessionTypes(), app.getActivityFormat(), 
                app.getUniversity(), app.getDegree(), app.getGraduationYear(),
                "#2f5283", "#eef1f7", 0, true
            );
        } else {
            req = new com.fanus.dto.PsychologistRequest(
                name, "Psixoloq", java.util.List.of(), "—", "0", "0.0",
                null, null, u.getPhone(), u.getEmail(),
                null, null, null, null, null, null,
                "#2f5283", "#eef1f7", 0, true
            );
        }
        psychologistService.create(req);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", name + " psixoloqlar siyahısına əlavə edildi"));
    }

    private UserDto toUserDto(User u) {
        boolean inList = "PSYCHOLOGIST".equals(u.getRole()) && psychologistRepository.existsByEmail(u.getEmail());
        return new UserDto(u.getId(), u.getEmail(), u.getRole(),
            u.getFirstName(), u.getLastName(), u.getPhone(),
            u.isEmailVerified(), inList, u.isActive(), u.getLastLogin(), u.getCreatedAt());
    }
}
