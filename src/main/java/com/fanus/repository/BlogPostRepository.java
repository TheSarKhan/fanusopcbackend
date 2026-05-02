package com.fanus.repository;

import com.fanus.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByActiveTrueAndStatusOrderByFeaturedDescPublishedDateDesc(String status);
    Optional<BlogPost> findBySlugAndActiveTrueAndStatus(String slug, String status);
    List<BlogPost> findTop10ByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query(
        "SELECT b.category, COUNT(b) FROM BlogPost b WHERE b.active = true AND b.status = 'PUBLISHED' GROUP BY b.category"
    )
    List<Object[]> countByCategory();
}
