package com.user.management.services.impl;

import com.user.management.exceptions.ValidationException;
import com.user.management.models.AuditLog;
import com.user.management.models.Keep;
import com.user.management.repositories.AuditLogRepository;
import com.user.management.services.IAuditLogs;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.user.management.enums.ResponseCode.INPUT_IS_INVALID;
import static com.user.management.util.UserManagementUtils.createValidationException;

@Service
@Slf4j
public class AuditLogService implements IAuditLogs {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Creates the audit logs for a keep, throwing an appropriate exception if something went wrong.
     *
     * @param username The username to first validate and then save.
     * @param keep     The keep details to first validate and then save.
     * @throws ServiceException Thrown if failed to create note.
     */
    @Override
    public void logKeepCreation(String username, Keep keep) {
        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(keep)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            AuditLog log = new AuditLog();
            log.setAction("CREATE_KEEP");
            log.setUsername(username);
            log.setKeepId(keep.getId());
            log.setKeepContent(keep.getContent());
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Error occurred while saving created audit log: {}", e.getMessage(), e);
            throw new ServiceException("Failed to save created audit log", e);
        }
    }

    /**
     * Updates the audit logs for a keep, throwing an appropriate exception if something went wrong.
     *
     * @param username The username to first validate and then save.
     * @param keep     The keep details to first validate and then save.
     * @throws ServiceException Thrown if failed to create note.
     */
    @Override
    public void logKeepUpdate(String username, Keep keep) {
        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(keep)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            AuditLog log = new AuditLog();
            log.setAction("UPDATE_KEEP");
            log.setUsername(username);
            log.setKeepId(keep.getId());
            log.setKeepContent(keep.getContent());
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Error occurred while saving updated audit log: {}", e.getMessage(), e);
            throw new ServiceException("Failed to save updated audit log", e);
        }
    }

    /**
     * Deletes the audit logs for a keep, throwing an appropriate exception if something went wrong.
     *
     * @param username The username to first validate and then save.
     * @param keepId   The keepId to first validate and then save deleted keep.
     * @throws ServiceException Thrown if failed to create note.
     */
    @Override
    public void logKeepDeletion(String username, Long keepId) {
        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(keepId)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            AuditLog log = new AuditLog();
            log.setAction("DELETE_KEEP");
            log.setUsername(username);
            log.setKeepId(keepId);
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Error occurred while saving deletion audit log: {}", e.getMessage(), e);
            throw new ServiceException("Failed to save deletion audit log", e);
        }
    }

    /**
     * To fetch all audit logs, throwing an appropriate exception if something went wrong.
     *
     * @throws ServiceException Thrown if failed to create note.
     */
    @Override
    public List<AuditLog> getAllAuditLogs() {
        try {
            return auditLogRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while fetching all audit logs: {}", e.getMessage(), e);
            throw new ServiceException("Failed to fetch all audit logs.", e);
        }
    }

    /**
     * To fetch all audit logs for a keepID, throwing an appropriate exception if something went wrong.
     *
     * @throws ServiceException Thrown if failed to create note.
     */
    @Override
    public List<AuditLog> getAuditLogsForKeepId(Long keepId) {
        try {
            return auditLogRepository.findByKeepId(keepId);
        } catch (Exception e) {
            log.error("Error occurred while fetching audit logs by keepID: {}", e.getMessage(), e);
            throw new ServiceException("Failed to fetch audit logs by keepID.", e);
        }
    }
}
