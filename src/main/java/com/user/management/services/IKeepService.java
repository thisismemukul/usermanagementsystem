package com.user.management.services;

import com.user.management.models.Keep;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IKeepService {

    Mono<Keep> createKeepForUser(String content, String username);

    Mono<Keep> updateKeepForUser(Long keepId, String content, String username);

    Mono<Void> deleteKeepForUser(Long keepId, String username);

    Flux<Keep> getAllKeepForUser(String username);

    Flux<Keep> getAllKeeps();

    Flux<Keep> getKeepByKeepId(Long keepId);
}

