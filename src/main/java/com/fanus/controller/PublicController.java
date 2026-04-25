package com.fanus.controller;

import com.fanus.dto.*;
import com.fanus.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicController {

    private final PsychologistService psychologistService;
    private final StatService statService;
    private final AnnouncementService announcementService;
    private final BlogPostService blogPostService;
    private final FaqService faqService;
    private final TestimonialService testimonialService;
    private final SiteConfigService siteConfigService;
    private final AppointmentService appointmentService;

    // Psychologists
    @GetMapping("/psychologists")
    public List<PsychologistDto> getPsychologists() {
        return psychologistService.findActive();
    }

    @GetMapping("/psychologists/{id}")
    public PsychologistDto getPsychologist(@PathVariable Long id) {
        return psychologistService.findById(id);
    }

    // Stats
    @GetMapping("/stats")
    public List<StatDto> getStats() {
        return statService.findAll();
    }

    // Announcements
    @GetMapping("/announcements")
    public List<AnnouncementDto> getAnnouncements() {
        return announcementService.findActive();
    }

    @GetMapping("/announcements/{id}")
    public AnnouncementDto getAnnouncement(@PathVariable Long id) {
        return announcementService.findById(id);
    }

    // Blog
    @GetMapping("/blog-posts")
    public List<BlogPostDto> getBlogPosts() {
        return blogPostService.findActive();
    }

    @GetMapping("/blog-posts/{slug}")
    public BlogPostDto getBlogPost(@PathVariable String slug) {
        return blogPostService.findBySlug(slug);
    }

    // FAQs
    @GetMapping("/faqs")
    public List<FaqDto> getFaqs() {
        return faqService.findActive();
    }

    // Testimonials
    @GetMapping("/testimonials")
    public List<TestimonialDto> getTestimonials() {
        return testimonialService.findActive();
    }

    // Site config
    @GetMapping("/site-config")
    public Map<String, String> getSiteConfig() {
        return siteConfigService.findAll();
    }

    // Appointments (booking)
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDto> book(@Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(req));
    }
}
