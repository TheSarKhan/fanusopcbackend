package com.fanus.service;

import com.fanus.dto.TestimonialDto;
import com.fanus.dto.TestimonialRequest;
import com.fanus.entity.Testimonial;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestimonialService {

    private final TestimonialRepository repo;

    public List<TestimonialDto> findActive() {
        return repo.findByActiveTrueOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    public List<TestimonialDto> findAll() {
        return repo.findAll().stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .map(this::toDto).toList();
    }

    @Transactional
    public TestimonialDto create(TestimonialRequest req) {
        Testimonial t = Testimonial.builder().quote(req.quote()).authorName(req.authorName())
            .authorRole(req.authorRole()).initials(req.initials()).gradient(req.gradient())
            .rating(req.rating()).active(req.active()).build();
        return toDto(repo.save(t));
    }

    @Transactional
    public TestimonialDto update(Long id, TestimonialRequest req) {
        Testimonial t = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Testimonial not found: " + id));
        t.setQuote(req.quote()); t.setAuthorName(req.authorName()); t.setAuthorRole(req.authorRole());
        t.setInitials(req.initials()); t.setGradient(req.gradient()); t.setRating(req.rating()); t.setActive(req.active());
        return toDto(repo.save(t));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Testimonial not found: " + id);
        repo.deleteById(id);
    }

    private TestimonialDto toDto(Testimonial t) {
        return new TestimonialDto(t.getId(), t.getQuote(), t.getAuthorName(), t.getAuthorRole(),
            t.getInitials(), t.getGradient(), t.getRating(), t.isActive());
    }
}
