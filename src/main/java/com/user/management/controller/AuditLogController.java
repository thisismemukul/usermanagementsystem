package com.user.management.controller;

import com.user.management.models.AuditLog;
import com.user.management.response.ApiResponse;
import com.user.management.services.impl.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.user.management.constants.RESTUriConstants.*;
import static com.user.management.util.UserManagementUtils.handleResponse;

@RestController
@RequestMapping(API_AUDIT)
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAllAuditLogs() {
        return handleResponse(auditLogService::getAllAuditLogs,
                "Created successfully",
                HttpStatus.CREATED);
    }

    @GetMapping(KEEP + KEEP_ID)
    public ResponseEntity<ApiResponse<List<AuditLog>>> getKeepAuditLogs(@PathVariable Long keepId) {
        return handleResponse(() ->
                        auditLogService.getAuditLogsForKeepId(keepId),
                "Created successfully",
                HttpStatus.CREATED);
    }


}
