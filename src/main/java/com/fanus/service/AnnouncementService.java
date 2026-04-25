package com.fanus.service;

import com.fanus.dto.AnnouncementDto;
import com.fanus.dto.AnnouncementRequest;
import com.fanus.entity.Announcement;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementService {

    private final AnnouncementRepository repo;

    public List<AnnouncementDto> findActive() {
        return repo.findByActiveTrueOrderByPublishedDateDesc().stream().map(this::toDto).toList();
    }

    public List<AnnouncementDto> findAll() {
        return repo.findAll().stream()
            .sorted((a, b) -> b.getPublishedDate().compareTo(a.getPublishedDate()))
            .map(this::toDto).toList();
    }

    public AnnouncementDto findById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id)));
    }

    @Transactional
    public AnnouncementDto create(AnnouncementRequest req) {
        Announcement a = Announcement.builder()
            .category(req.category()).categoryColor(req.categoryColor()).categoryBg(req.categoryBg())
            .title(req.title()).excerpt(req.excerpt()).publishedDate(req.publishedDate())
            .iconType(req.iconType()).active(req.active()).build();
        return toDto(repo.save(a));
    }

    @Transactional
    public AnnouncementDto update(Long id, AnnouncementRequest req) {
        Announcement a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id));
        a.setCategory(req.category()); a.setCategoryColor(req.categoryColor()); a.setCategoryBg(req.categoryBg());
        a.setTitle(req.title()); a.setExcerpt(req.excerpt()); a.setPublishedDate(req.publishedDate());
        a.setIconType(req.iconType()); a.setActive(req.active());
        return toDto(repo.save(a));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Announcement not found: " + id);
        repo.deleteById(id);
    }

    private AnnouncementDto toDto(Announcement a) {
        return new AnnouncementDto(a.getId(), a.getCategory(), a.getCategoryColor(), a.getCategoryBg(),
            a.getTitle(), a.getExcerpt(), a.getPublishedDate(), a.getIconType(), a.isActive());
    }
}
