package com.fanus.service;

import com.fanus.dto.BlogCategoryDto;
import com.fanus.dto.BlogCategoryRequest;
import com.fanus.entity.BlogCategory;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.BlogCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogCategoryService {

    private final BlogCategoryRepository repo;

    public List<BlogCategoryDto> findActive() {
        return repo.findByActiveTrueOrderBySortOrderAscNameAsc().stream().map(this::toDto).toList();
    }

    public List<BlogCategoryDto> findAll() {
        return repo.findAllByOrderBySortOrderAscNameAsc().stream().map(this::toDto).toList();
    }

    @Transactional
    public BlogCategoryDto create(BlogCategoryRequest req) {
        BlogCategory c = BlogCategory.builder()
            .name(req.name())
            .color(req.color())
            .bg(req.bg())
            .emoji(req.emoji() != null ? req.emoji() : "📝")
            .active(req.active())
            .sortOrder(req.sortOrder())
            .build();
        return toDto(repo.save(c));
    }

    @Transactional
    public BlogCategoryDto update(Long id, BlogCategoryRequest req) {
        BlogCategory c = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        c.setName(req.name());
        c.setColor(req.color());
        c.setBg(req.bg());
        c.setEmoji(req.emoji() != null ? req.emoji() : "📝");
        c.setActive(req.active());
        c.setSortOrder(req.sortOrder());
        return toDto(repo.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Category not found: " + id);
        repo.deleteById(id);
    }

    private BlogCategoryDto toDto(BlogCategory c) {
        return new BlogCategoryDto(c.getId(), c.getName(), c.getColor(), c.getBg(),
            c.getEmoji(), c.isActive(), c.getSortOrder());
    }
}
