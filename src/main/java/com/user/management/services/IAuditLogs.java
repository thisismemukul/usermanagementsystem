package com.user.management.services;

import com.user.management.models.AuditLog;
import com.user.management.models.Keep;

import java.util.List;

public interface IAuditLogs {

    void logKeepCreation(String username, Keep keep);

    void logKeepUpdate(String username, Keep keep);

    void logKeepDeletion(String username, Long keepId);

    List<AuditLog> getAllAuditLogs();

    List<AuditLog> getAuditLogsForKeepId(Long keepId);
}
