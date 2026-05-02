package com.fanus.repository;

import com.fanus.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {
    List<BlogCategory> findByActiveTrueOrderBySortOrderAscNameAsc();
    List<BlogCategory> findAllByOrderBySortOrderAscNameAsc();
}
