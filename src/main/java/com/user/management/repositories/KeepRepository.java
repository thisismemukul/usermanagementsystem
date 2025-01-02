package com.user.management.repositories;

import com.user.management.models.Keep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeepRepository extends JpaRepository<Keep, Long> {
    List<Keep> findByOwnerUsername(String ownerUsername);
}
