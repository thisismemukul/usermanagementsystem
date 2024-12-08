package com.user.management.services.impl;

import com.user.management.exceptions.UserMgmtException;
import com.user.management.exceptions.ValidationException;
import com.user.management.models.Keep;
import com.user.management.repositories.KeepRepository;
import com.user.management.services.IKeepService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static com.user.management.enums.ResponseCode.CONTENT_IS_EMPTY;
import static com.user.management.enums.ResponseCode.USER_DETAILS_MISSING;
import static com.user.management.util.UserManagementUtils.*;

@Service
@Validated
@Slf4j
public class KeepService implements IKeepService {

    private final KeepRepository keepRepository;

    public KeepService(KeepRepository keepRepository) {
        this.keepRepository = keepRepository;
    }

    /**
     * Creates the notes for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param content      The content to first validate and then save.
     * @param userDetails  The user details to first validate and then save.
     * @exception ServiceException Thrown if failed to create note.
     */
    @Override
    public Keep createKeepForUser(String content, UserDetails userDetails) throws ValidationException {
        validateContentAndUser(content, userDetails);
        try {
            String username = userDetails.getUsername();
            Keep savedKeep = keepRepository.save(new Keep(content, username));
            log.info("Note created successfully with ID: {}", savedKeep.getId());
            return savedKeep;
        } catch (Exception e) {
            log.error("Error occurred while creating note: {}", e.getMessage(), e);
            throw new ServiceException("Failed to create note", e);
        }
    }

    /**
     * Updates the notes for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param noteId      The noteId to first validate and to fetch existing note.
     * @param content      The content to first validate and then save.
     * @param userDetails  The user details to first validate and to fetch existing note.
     * @exception ServiceException Thrown if failed to update note.
     */
    @Override
    public Keep updateKeepForUser(Long noteId, String content, UserDetails userDetails) {
        validateNoteIdContentAndUser(noteId, content, userDetails);
        try {
            Keep existingKeep = getExistingKeep(noteId, userDetails);
            existingKeep.setContent(content);
            Keep updatedKeep = keepRepository.save(existingKeep);
            log.info("Note updated successfully with ID: {}", updatedKeep.getId());
            return updatedKeep;
        } catch (Exception e) {
            log.error("Error occurred while updating note: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update note", e);
        }
    }

    /**
     * Delete the note for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param keepId      The keepId to first validate and to fetch existing note.
     * @param userDetails  The user details to first validate and to fetch existing note.
     * @exception ServiceException Thrown if failed to delete note.
     */
    @Override
    public void deleteKeepForUser(Long keepId, UserDetails userDetails) {
        validateKeepIdAndUser(keepId, userDetails);
        try {
            Keep existingKeep = getExistingKeep(keepId, userDetails);
            keepRepository.delete(existingKeep);
            log.info("Note deleted successfully with ID: {}", keepId);
        } catch (Exception e) {
            log.error("Error occurred while deleting note: {}", e.getMessage(), e);
            throw new ServiceException("Failed to delete note", e);
        }
    }

    /**
     * To fetch all the notes for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userDetails  The user details to first validate and to fetch existing note.
     * @exception UserMgmtException Thrown if unable to extract user details.
     * @exception ServiceException Thrown if failed to retrieve notes for user.
     */
    @Override
    public List<Keep> getAllKeepForUser(UserDetails userDetails) {
        if (ObjectUtils.isEmpty(userDetails.getUsername())) {
            throw createUserMgmtException(USER_DETAILS_MISSING);
        }

        String username = userDetails.getUsername();
        try {
            List<Keep> keeps = keepRepository.findByOwnerUsername(username);
            log.info("Retrieved {} notes for user {}", keeps.size(), username);
            return keeps;
        } catch (Exception e) {
            log.error("Error occurred while retrieving notes for user {}: {}", username, e.getMessage(), e);
            throw new ServiceException("Failed to retrieve notes for user: " + username, e);
        }
    }

    /**
     * To fetch all the notes from database, throwing an appropriate exception if something went wrong.
     *
     * @exception ServiceException Thrown if failed to all notes.
     */
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

    /**
     * To fetch all the note by note ID, throwing an appropriate exception if something went wrong.
     *
     * @param keepId      The keepId to first validate and to fetch existing note.
     * @exception ValidationException Thrown if content is empty.
     * @exception ServiceException Thrown if failed to retrieve notes for user.
     */
    @Override
    public Optional<Keep> getKeepByKeepId(Long keepId) {
        if (ObjectUtils.isEmpty(keepId)) {
            throw createValidationException(CONTENT_IS_EMPTY);
        }
        try {
            return keepRepository.findById(keepId)
                    .map(keep -> {
                        log.info("Retrieved note with ID: {}", keepId);
                        return keep;
                    })
                    .or(() -> {
                        log.warn("No note found with ID: {}", keepId);
                        return Optional.empty();
                    });
        } catch (Exception e) {
            log.error("Error occurred while retrieving note by ID: {}", e.getMessage(), e);
            throw new ServiceException("Failed to retrieve note by ID.", e);
        }
    }

    /**
     * To fetch all the existing note by note ID, throwing an appropriate exception if something went wrong.
     *
     * @param keepId      The keepId to first validate and to fetch existing note.
     * @exception UserMgmtException Thrown if unable to extract user details.
     * @exception ServiceException Thrown if failed to retrieve notes for user.
     */
    private Keep getExistingKeep(Long keepId, UserDetails userDetails) {
        validateKeepIdAndUser(keepId, userDetails);

        return keepRepository.findById(keepId)
                .filter(keep -> keep.getOwnerUsername().equals(userDetails.getUsername()))
                .orElseThrow(() -> {
                    log.warn("Note not found or unauthorized access for ID: {} by user: {}", keepId, userDetails.getUsername());
                    return new ServiceException("Note not found or unauthorized access");
                });
    }
}