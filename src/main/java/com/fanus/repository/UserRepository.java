package com.fanus.repository;

import com.fanus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String token);

    long countByRole(String role);

    long countByCreatedAtAfter(java.time.LocalDateTime since);
}
