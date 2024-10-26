package com.user.management.services.impl;

import com.user.management.models.Keep;
import com.user.management.repositories.KeepRepository;
import com.user.management.services.IKeepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Validated
@Slf4j
public class KeepService implements IKeepService {

    private final KeepRepository keepRepository;

    public KeepService(KeepRepository keepRepository) {
        this.keepRepository = keepRepository;
    }

    @Override
    public Mono<Keep> createKeepForUser(String content, String username) {
        return Mono.fromCallable(() -> {
                    Keep keep = new Keep();
                    keep.setContent(content);
                    keep.setOwnerUsername(username);
                    return keepRepository.save(keep);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(savedKeep -> log.info("Note created successfully with ID: {}", savedKeep.getId()))
                .doOnError(error -> log.error("Failed to create note", error));
    }

    @Override
    public Mono<Keep> updateKeepForUser(Long noteId, String content, String username) {
        return Mono.fromCallable(() -> {
                    Keep existingKeep = keepRepository.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found"));
                    if (!existingKeep.getOwnerUsername().equals(username)) {
                        throw new RuntimeException("Unauthorized access");
                    }
                    existingKeep.setContent(content);
                    return keepRepository.save(existingKeep);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("Error updating note with ID: {}", noteId, error));
    }

    @Override
    public Mono<Void> deleteKeepForUser(Long noteId, String username) {
        return Mono.fromCallable(() -> {
                    Keep existingKeep = keepRepository.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found"));
                    if (!existingKeep.getOwnerUsername().equals(username)) {
                        throw new RuntimeException("Unauthorized access");
                    }
                    keepRepository.delete(existingKeep);
                    return Void.TYPE;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then()
                .doOnError(error -> log.error("Error deleting note with ID: {}", noteId, error));
    }

    @Override
    public Flux<Keep> getAllKeepForUser(String username) {
        return Mono.fromCallable(() -> keepRepository.findByOwnerUsername(username))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .doOnError(error -> log.error("Error retrieving notes", error));
    }

    @Override
    public Flux<Keep> getAllKeeps() {
        return Mono.fromCallable(keepRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .doOnError(error -> log.error("Error retrieving notes", error));
    }

    @Override
    public Flux<Keep> getKeepByKeepId(Long keepId) {
        return Mono.fromCallable(() -> keepRepository.findById(keepId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(optionalKeep ->
                        Mono.justOrEmpty(optionalKeep)
                                .flux()
                                .doOnError(error -> log.error("Error retrieving notes", error))
                );
    }

}
