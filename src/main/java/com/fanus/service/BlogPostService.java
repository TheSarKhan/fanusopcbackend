package com.fanus.service;

import com.fanus.dto.BlogPostDto;
import com.fanus.dto.BlogPostRequest;
import com.fanus.entity.BlogPost;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogPostService {

    private final BlogPostRepository repo;

    public List<BlogPostDto> findActive() {
        return repo.findByActiveTrueOrderByFeaturedDescPublishedDateDesc().stream().map(this::toDto).toList();
    }

    public List<BlogPostDto> findAll() {
        return repo.findAll().stream()
            .sorted((a, b) -> b.getPublishedDate().compareTo(a.getPublishedDate()))
            .map(this::toDto).toList();
    }

    public BlogPostDto findBySlug(String slug) {
        return toDto(repo.findBySlugAndActiveTrue(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Blog post not found: " + slug)));
    }

    public BlogPostDto findById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog post not found: " + id)));
    }

    @Transactional
    public BlogPostDto create(BlogPostRequest req) {
        BlogPost p = BlogPost.builder()
            .category(req.category()).categoryColor(req.categoryColor()).categoryBg(req.categoryBg())
            .title(req.title()).excerpt(req.excerpt()).readTimeMinutes(req.readTimeMinutes())
            .publishedDate(req.publishedDate()).emoji(req.emoji()).slug(req.slug())
            .featured(req.featured()).active(req.active()).build();
        return toDto(repo.save(p));
    }

    @Transactional
    public BlogPostDto update(Long id, BlogPostRequest req) {
        BlogPost p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Blog post not found: " + id));
        p.setCategory(req.category()); p.setCategoryColor(req.categoryColor()); p.setCategoryBg(req.categoryBg());
        p.setTitle(req.title()); p.setExcerpt(req.excerpt()); p.setReadTimeMinutes(req.readTimeMinutes());
        p.setPublishedDate(req.publishedDate()); p.setEmoji(req.emoji()); p.setSlug(req.slug());
        p.setFeatured(req.featured()); p.setActive(req.active());
        return toDto(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Blog post not found: " + id);
        repo.deleteById(id);
    }

    private BlogPostDto toDto(BlogPost p) {
        return new BlogPostDto(p.getId(), p.getCategory(), p.getCategoryColor(), p.getCategoryBg(),
            p.getTitle(), p.getExcerpt(), p.getReadTimeMinutes(), p.getPublishedDate(),
            p.getEmoji(), p.getSlug(), p.isFeatured(), p.isActive());
    }
}
