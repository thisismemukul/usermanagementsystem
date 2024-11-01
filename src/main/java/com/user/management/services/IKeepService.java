package com.user.management.services;

import com.user.management.models.Keep;

import java.util.List;
import java.util.Optional;

public interface IKeepService {

    Keep createKeepForUser(String content, String username);

    Keep updateKeepForUser(Long keepId, String content, String username);

    void deleteKeepForUser(Long keepId, String username);

    List<Keep> getAllKeepForUser(String username);

    List<Keep> getAllKeeps();

    Optional<Keep> getKeepByKeepId(Long keepId);
}
