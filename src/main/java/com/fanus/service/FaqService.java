package com.fanus.service;

import com.fanus.dto.FaqDto;
import com.fanus.dto.FaqRequest;
import com.fanus.entity.Faq;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

    private final FaqRepository repo;

    public List<FaqDto> findActive() {
        return repo.findByActiveTrueOrderByDisplayOrderAsc().stream().map(this::toDto).toList();
    }

    public List<FaqDto> findAll() {
        return repo.findAll().stream()
            .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
            .map(this::toDto).toList();
    }

    @Transactional
    public FaqDto create(FaqRequest req) {
        Faq f = Faq.builder().question(req.question()).answer(req.answer())
            .displayOrder(req.displayOrder()).active(req.active()).build();
        return toDto(repo.save(f));
    }

    @Transactional
    public FaqDto update(Long id, FaqRequest req) {
        Faq f = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("FAQ not found: " + id));
        f.setQuestion(req.question()); f.setAnswer(req.answer());
        f.setDisplayOrder(req.displayOrder()); f.setActive(req.active());
        return toDto(repo.save(f));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("FAQ not found: " + id);
        repo.deleteById(id);
    }

    private FaqDto toDto(Faq f) {
        return new FaqDto(f.getId(), f.getQuestion(), f.getAnswer(), f.getDisplayOrder(), f.isActive());
    }
}
