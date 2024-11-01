package com.user.management.controller;

import com.user.management.models.Keep;
import com.user.management.request.KeepRequest;
import com.user.management.response.ApiResponse;
import com.user.management.services.impl.KeepService;
import com.user.management.util.UserManagementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/keeps")
public class KeepController {

    private final KeepService keepService;

    public KeepController(KeepService keepService) {
        this.keepService = keepService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Keep>> createKeep(@RequestBody KeepRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return handleResponse(() ->
                        keepService.createKeepForUser(
                                request.getContent(),
                                username),
                "Created successfully",
                HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<Keep>>> getUserKeeps(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return handleResponse(() ->
                        keepService.getAllKeepForUser(username),
                "All kept notes",
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Keep>>> getAllKeeps() {
        return handleResponse(
                keepService::getAllKeeps,
                "All kept notes",
                HttpStatus.OK);
    }

    @PutMapping("/{keepId}")
    public ResponseEntity<ApiResponse<Keep>> updateKeep(@PathVariable Long keepId, @RequestBody KeepRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return handleResponse(() ->
                        keepService.updateKeepForUser(
                                keepId,
                                request.getContent(),
                                username),
                "Keep updated successfully",
                HttpStatus.OK);
    }

    @DeleteMapping("/{keepId}")
    public ResponseEntity<ApiResponse<Object>> deleteKeep(@PathVariable Long keepId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return handleResponse(() -> {
            keepService.deleteKeepForUser(keepId, username);
            return null;
        }, "Keep deleted successfully", HttpStatus.OK);
    }

    private <T> ResponseEntity<ApiResponse<T>> handleResponse(Supplier<T> supplier, String successMessage, HttpStatus status) {
        try {
            T result = supplier.get();
            return UserManagementUtils.successResponse(successMessage, status, result);
        } catch (Exception e) {
            return UserManagementUtils.errorResponse(e);
        }
    }
}

