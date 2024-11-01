package com.user.management.services.impl;

import com.user.management.models.Keep;
import com.user.management.repositories.KeepRepository;
import com.user.management.services.IKeepService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@Slf4j
public class KeepService implements IKeepService {

    private final KeepRepository keepRepository;

    public KeepService(KeepRepository keepRepository) {
        this.keepRepository = keepRepository;
    }

    @Override
    public Keep createKeepForUser(String content, String username) {
        try {
            Keep savedKeep = keepRepository.save(Keep.builder()
                    .content(content)
                    .ownerUsername(username)
                    .build());
            log.info("Note created successfully with ID: {}", savedKeep.getId());
            return savedKeep;
        } catch (Exception e) {
            log.error("Error occurred while creating note: {}", e.getMessage(), e);
            throw new ServiceException("Failed to create note", e);
        }
    }

    @Override
    public Keep updateKeepForUser(Long noteId, String content, String username) {
        Keep existingKeep = getExistingKeep(noteId, username);
        existingKeep.setContent(content);
        Keep updatedKeep = keepRepository.save(existingKeep);
        log.info("Note updated successfully with ID: {}", updatedKeep.getId());
        return updatedKeep;
    }

    @Override
    public void deleteKeepForUser(Long noteId, String username) {
        Keep existingKeep = getExistingKeep(noteId, username);
        keepRepository.delete(existingKeep);
        log.info("Note deleted successfully with ID: {}", noteId);
    }

    @Override
    public List<Keep> getAllKeepForUser(String username) {
        try {
            List<Keep> keeps = keepRepository.findByOwnerUsername(username);
            log.info("Retrieved {} notes for user {}", keeps.size(), username);
            return keeps;
        } catch (Exception e) {
            log.error("Error occurred while retrieving notes for user {}: {}", username, e.getMessage(), e);
            throw new ServiceException("Failed to retrieve notes for user: " + username, e);
        }
    }

    @Override
    public List<Keep> getAllKeeps() {
        try {
            List<Keep> keeps = keepRepository.findAll();
            log.info("Retrieved all notes, total count: {}", keeps.size());
            return keeps;
        } catch (Exception e) {
            log.error("Error occurred while retrieving all notes: {}", e.getMessage(), e);
            throw new ServiceException("Failed to retrieve all notes", e);
        }
    }

    @Override
    public Optional<Keep> getKeepByKeepId(Long keepId) {
        return keepRepository.findById(keepId)
                .map(keep -> {
                    log.info("Retrieved note with ID: {}", keepId);
                    return keep;
                })
                .or(() -> {
                    log.warn("No note found with ID: {}", keepId);
                    return Optional.empty();
                });
    }

    private Keep getExistingKeep(Long noteId, String username) {
        return keepRepository.findById(noteId).filter(keep -> keep.getOwnerUsername().equals(username))
                .orElseThrow(() -> {
                    log.warn("Note not found or unauthorized access for ID: {} by user: {}", noteId, username);
                    return new ServiceException("Note not found or unauthorized access");
                });
    }
}
