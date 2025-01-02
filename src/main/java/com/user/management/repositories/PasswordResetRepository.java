package com.user.management.repositories;

import com.user.management.models.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
     Optional<PasswordReset> findByToken(String token);
}
