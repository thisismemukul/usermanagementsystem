package com.user.management.services;

import com.user.management.models.Keep;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface IKeepService {

    Keep createKeepForUser(String content, UserDetails userDetails);

    Keep updateKeepForUser(Long keepId, String content, UserDetails userDetails);

    void deleteKeepForUser(Long keepId, UserDetails userDetails);

    List<Keep> getAllKeepForUser(UserDetails userDetails);

    List<Keep> getAllKeeps();

    Optional<Keep> getKeepByKeepId(Long keepId);
}
