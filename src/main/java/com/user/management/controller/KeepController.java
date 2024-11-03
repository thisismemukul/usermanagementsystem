package com.user.management.controller;

import com.user.management.models.Keep;
import com.user.management.request.KeepRequest;
import com.user.management.response.ApiResponse;
import com.user.management.services.impl.KeepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.user.management.constants.RESTUriConstants.*;
import static com.user.management.util.UserManagementUtils.handleResponse;

@RestController
@RequestMapping(API_KEEPS)
public class KeepController {

    private final KeepService keepService;

    public KeepController(KeepService keepService) {
        this.keepService = keepService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Keep>> createKeep(@RequestBody KeepRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return handleResponse(() ->
                        keepService.createKeepForUser(
                                request.getContent(),
                                userDetails),
                "Created successfully",
                HttpStatus.CREATED);
    }

    @GetMapping(USER)
    public ResponseEntity<ApiResponse<List<Keep>>> getUserKeeps(@AuthenticationPrincipal UserDetails userDetails) {
        return handleResponse(() ->
                        keepService.getAllKeepForUser(userDetails),
                "All kept notes",
                HttpStatus.OK);
    }


    @GetMapping(KEEP_ID)
    public ResponseEntity<ApiResponse<Optional<Keep>>> getUserKeepsById(@PathVariable Long keepId) {
        return handleResponse(() ->
                        keepService.getKeepByKeepId(keepId),
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

    @PutMapping(KEEP_ID)
    public ResponseEntity<ApiResponse<Keep>> updateKeep(@PathVariable Long keepId, @RequestBody KeepRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return handleResponse(() ->
                        keepService.updateKeepForUser(
                                keepId,
                                request.getContent(),
                                userDetails),
                "Keep updated successfully",
                HttpStatus.OK);
    }

    @DeleteMapping(KEEP_ID)
    public ResponseEntity<ApiResponse<Object>> deleteKeep(@PathVariable Long keepId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return handleResponse(() -> {
            keepService.deleteKeepForUser(keepId, userDetails);
            return null;
        }, "Keep deleted successfully", HttpStatus.OK);
    }

}

