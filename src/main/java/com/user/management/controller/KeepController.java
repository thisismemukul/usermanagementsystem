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
import reactor.core.publisher.Mono;

import java.util.List;

import static com.user.management.util.UserManagementUtils.successResponse;

@RestController
@RequestMapping("/api/keeps")
public class KeepController {

    private final KeepService keepService;

    public KeepController(KeepService keepService) {
        this.keepService = keepService;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Keep>>> createKeep(@RequestBody KeepRequest request,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        return keepService.createKeepForUser(request.getContent(), username)
                .flatMap(result -> successResponse("Created successfully", HttpStatus.CREATED, result))
                .onErrorResume(UserManagementUtils::errorResponse);
    }

    @GetMapping("/user")
    public Mono<ResponseEntity<ApiResponse<List<Keep>>>> getUserKeeps(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        return keepService.getAllKeepForUser(username)
                .collectList()
                .flatMap(result -> successResponse("All kept notes", HttpStatus.OK, result))
                .onErrorResume(UserManagementUtils::errorResponse);
    }


    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<Keep>>>> getAllKeeps() {

        return keepService.getAllKeeps()
                .collectList()
                .flatMap(result -> successResponse("All kept notes", HttpStatus.OK, result))
                .onErrorResume(UserManagementUtils::errorResponse);
    }

    @GetMapping("/{keepId}")
    public Mono<ResponseEntity<ApiResponse<List<Keep>>>> getKeepByKeepId(@PathVariable Long keepId) {
        return keepService.getKeepByKeepId(keepId)
                .collectList()
                .flatMap(result -> successResponse("Kept notes", HttpStatus.OK, result))
                .onErrorResume(UserManagementUtils::errorResponse);
    }

    @PutMapping("/{keepId}")
    public Mono<ResponseEntity<ApiResponse<Keep>>> updateKeep(@PathVariable Long keepId, @RequestBody KeepRequest request,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return keepService.updateKeepForUser(keepId, request.getContent(), username)
                .flatMap(result -> successResponse("Keep updated successfully", HttpStatus.OK, result))
                .onErrorResume(UserManagementUtils::errorResponse);
    }

    @DeleteMapping("/{keepId}")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteKeep(@PathVariable Long keepId,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return keepService.deleteKeepForUser(keepId, username)
                .then(UserManagementUtils.successResponse("Keep deleted successfully", HttpStatus.OK, null))
                .onErrorResume(UserManagementUtils::errorResponse);
    }

}
