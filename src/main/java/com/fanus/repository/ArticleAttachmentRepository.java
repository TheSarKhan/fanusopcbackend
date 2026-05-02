package com.fanus.repository;

import com.fanus.entity.ArticleAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleAttachmentRepository extends JpaRepository<ArticleAttachment, Long> {
    List<ArticleAttachment> findByArticleIdOrderByDisplayOrderAsc(Long articleId);
    void deleteByArticleIdAndId(Long articleId, Long id);
}
