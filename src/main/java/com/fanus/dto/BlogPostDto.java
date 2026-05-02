package com.fanus.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BlogPostDto(
    Long id,
    String category,
    String categoryColor,
    String categoryBg,
    String title,
    String excerpt,
    String content,
    String coverImageUrl,
    int readTimeMinutes,
    LocalDate publishedDate,
    String emoji,
    String slug,
    boolean featured,
    boolean active,
    String status,
    Long authorId,
    String authorName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<ArticleAttachmentDto> attachments,
    // Draft shadow fields — only populated for admin; public API ignores these
    String draftTitle,
    String draftContent,
    String draftCoverImageUrl,
    String draftExcerpt,
    boolean hasPendingDraft
) {}
