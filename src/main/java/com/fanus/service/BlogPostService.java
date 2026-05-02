package com.fanus.service;

import com.fanus.dto.ArticleAttachmentDto;
import com.fanus.dto.BlogPostDto;
import com.fanus.dto.BlogPostRequest;
import com.fanus.entity.BlogPost;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogPostService {

    private final BlogPostRepository repo;

    public List<BlogPostDto> findActive() {
        return repo.findByActiveTrueAndStatusOrderByFeaturedDescPublishedDateDesc("PUBLISHED")
            .stream().map(this::toDto).toList();
    }

    public List<BlogPostDto> findAll() {
        return repo.findAll().stream()
            .sorted(Comparator.comparing(BlogPost::getCreatedAt).reversed())
            .map(this::toDto).toList();
    }

    public BlogPostDto findBySlug(String slug) {
        return toDto(repo.findBySlugAndActiveTrueAndStatus(slug, "PUBLISHED")
            .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + slug)));
    }

    public BlogPostDto findById(Long id) {
        return toDto(repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id)));
    }

    @Transactional
    public BlogPostDto create(BlogPostRequest req) {
        String status = req.status() != null ? req.status() : "PUBLISHED";
        BlogPost p = BlogPost.builder()
            .category(req.category()).categoryColor(req.categoryColor()).categoryBg(req.categoryBg())
            .title(req.title())
            .excerpt(req.excerpt() != null ? req.excerpt() : autoExcerpt(req.content()))
            .content(req.content())
            .coverImageUrl(req.coverImageUrl())
            .readTimeMinutes(autoReadTime(req.content()))
            .publishedDate(req.publishedDate())
            .emoji(req.emoji() != null ? req.emoji() : "📝")
            .slug(req.slug())
            .featured(req.featured())
            .active(req.active())
            .status(status)
            .build();
        return toDto(repo.save(p));
    }

    @Transactional
    public BlogPostDto update(Long id, BlogPostRequest req) {
        BlogPost p = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id));

        boolean isPublished = "PUBLISHED".equals(p.getStatus());
        boolean wantsPublish = "PUBLISHED".equals(req.status());

        if (isPublished && !wantsPublish) {
            // Auto-save on a live article → save to shadow draft fields only; live content untouched
            p.setDraftTitle(req.title());
            p.setDraftContent(req.content());
            p.setDraftCoverImageUrl(req.coverImageUrl());
            p.setDraftExcerpt(req.excerpt());
            p.setHasPendingDraft(true);
        } else if (wantsPublish) {
            // Explicit publish: apply pending draft (or incoming data) to live fields
            String title   = p.isHasPendingDraft() && p.getDraftTitle()   != null ? p.getDraftTitle()   : req.title();
            String content = p.isHasPendingDraft() && p.getDraftContent() != null ? p.getDraftContent() : req.content();
            String cover   = p.isHasPendingDraft() && p.getDraftCoverImageUrl() != null ? p.getDraftCoverImageUrl() : req.coverImageUrl();
            String excerpt = p.isHasPendingDraft() && p.getDraftExcerpt() != null ? p.getDraftExcerpt() : req.excerpt();

            p.setTitle(title);
            p.setContent(content);
            p.setCoverImageUrl(cover);
            p.setExcerpt(excerpt != null && !excerpt.isBlank() ? excerpt : autoExcerpt(content));
            p.setReadTimeMinutes(autoReadTime(content));

            // Clear draft shadow
            p.setDraftTitle(null); p.setDraftContent(null);
            p.setDraftCoverImageUrl(null); p.setDraftExcerpt(null);
            p.setHasPendingDraft(false);

            // Update non-content live fields
            p.setCategory(req.category()); p.setCategoryColor(req.categoryColor()); p.setCategoryBg(req.categoryBg());
            p.setPublishedDate(req.publishedDate());
            p.setEmoji(req.emoji() != null ? req.emoji() : "📝");
            p.setSlug(req.slug());
            p.setFeatured(req.featured());
            p.setActive(req.active());
            p.setStatus("PUBLISHED");
        } else {
            // Normal update of a DRAFT article
            p.setCategory(req.category()); p.setCategoryColor(req.categoryColor()); p.setCategoryBg(req.categoryBg());
            p.setTitle(req.title());
            p.setExcerpt(req.excerpt() != null && !req.excerpt().isBlank() ? req.excerpt() : autoExcerpt(req.content()));
            p.setContent(req.content());
            p.setCoverImageUrl(req.coverImageUrl());
            p.setReadTimeMinutes(autoReadTime(req.content()));
            p.setPublishedDate(req.publishedDate());
            p.setEmoji(req.emoji() != null ? req.emoji() : "📝");
            p.setSlug(req.slug());
            p.setFeatured(req.featured());
            p.setActive(req.active());
            if (req.status() != null) p.setStatus(req.status());
        }

        return toDto(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Article not found: " + id);
        repo.deleteById(id);
    }

    private int autoReadTime(String content) {
        if (content == null || content.isBlank()) return 1;
        String text = content.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        int words = text.split(" ").length;
        return Math.max(1, (int) Math.ceil(words / 200.0));
    }

    private String autoExcerpt(String content) {
        if (content == null || content.isBlank()) return "";
        String text = content.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }

    private BlogPostDto toDto(BlogPost p) {
        String authorName = null;
        Long authorId = null;
        if (p.getAuthor() != null) {
            authorId = p.getAuthor().getId();
            String first = p.getAuthor().getFirstName() != null ? p.getAuthor().getFirstName() : "";
            String last = p.getAuthor().getLastName() != null ? p.getAuthor().getLastName() : "";
            authorName = (first + " " + last).trim();
        }
        List<ArticleAttachmentDto> attachments = p.getAttachments() == null ? List.of() :
            p.getAttachments().stream()
                .map(a -> new ArticleAttachmentDto(a.getId(), a.getFileUrl(), a.getFileName(),
                    a.getFileType(), a.getFileSizeBytes(), a.getDisplayOrder()))
                .toList();
        return new BlogPostDto(
            p.getId(), p.getCategory(), p.getCategoryColor(), p.getCategoryBg(),
            p.getTitle(), p.getExcerpt(), p.getContent(), p.getCoverImageUrl(),
            p.getReadTimeMinutes(), p.getPublishedDate(), p.getEmoji(), p.getSlug(),
            p.isFeatured(), p.isActive(),
            p.getStatus() != null ? p.getStatus() : "PUBLISHED",
            authorId, authorName,
            p.getCreatedAt(), p.getUpdatedAt(),
            attachments,
            p.getDraftTitle(), p.getDraftContent(), p.getDraftCoverImageUrl(),
            p.getDraftExcerpt(), p.isHasPendingDraft()
        );
    }
}
