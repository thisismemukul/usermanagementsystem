package com.user.management.controller;

import com.user.management.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.user.management.util.UserManagementUtils.errorResponse;
import static com.user.management.util.UserManagementUtils.successResponse;

@RestController
@RequestMapping("/api")
public class CsrfController {

    @GetMapping("/csrf-token")
    public ResponseEntity<ApiResponse<CsrfToken>> csrfToken(HttpServletRequest request) {
        return Optional.ofNullable((CsrfToken) request.getAttribute(CsrfToken.class.getName()))
                .map(csrfToken -> successResponse("CSRF token retrieved successfully", HttpStatus.OK, csrfToken))
                .orElseGet(() -> errorResponse(new RuntimeException("CSRF token not found")));
    }
}
