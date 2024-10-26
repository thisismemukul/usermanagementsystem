package com.user.management.repositories;

import com.user.management.models.Keep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeepRepository extends JpaRepository<Keep, Long> {
    List<Keep> findByOwnerUsername(String ownerUsername);
}
