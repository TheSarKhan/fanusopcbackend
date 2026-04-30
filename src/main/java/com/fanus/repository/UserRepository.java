package com.fanus.repository;

import com.fanus.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String token);

    long countByRole(String role);
    long countByCreatedAtAfter(java.time.LocalDateTime since);

    List<User> findAllByOrderByCreatedAtDesc();

    // Paginated: no role filter, optional search
    @Query("SELECT u FROM User u WHERE " +
           "(:q IS NULL OR LOWER(u.email) LIKE %:q% OR LOWER(u.firstName) LIKE %:q% OR LOWER(u.lastName) LIKE %:q% OR u.phone LIKE %:q%)")
    Page<User> findAllFiltered(@Param("q") String q, Pageable pageable);

    // Paginated: with role filter, optional search
    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(:q IS NULL OR LOWER(u.email) LIKE %:q% OR LOWER(u.firstName) LIKE %:q% OR LOWER(u.lastName) LIKE %:q% OR u.phone LIKE %:q%)")
    Page<User> findByRoleFiltered(@Param("role") String role, @Param("q") String q, Pageable pageable);

    // Counts per role for stat cards
    long countByRoleAndActive(String role, boolean active);
}
