package com.fanus.repository;

import com.fanus.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByActiveTrueOrderByFeaturedDescPublishedDateDesc();
    Optional<BlogPost> findBySlugAndActiveTrue(String slug);

    List<BlogPost> findTop10ByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query(
        "SELECT b.category, COUNT(b) FROM BlogPost b WHERE b.active = true GROUP BY b.category"
    )
    List<Object[]> countByCategory();
}
