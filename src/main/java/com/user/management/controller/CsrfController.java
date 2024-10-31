package com.user.management.controller;

import com.user.management.response.ApiResponse;
import com.user.management.util.UserManagementUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.user.management.util.UserManagementUtils.errorResponse;
import static com.user.management.util.UserManagementUtils.successResponse;

@RestController
@RequestMapping("/api")
public class CsrfController {

    @GetMapping("/csrf-token")
    public Mono<ResponseEntity<ApiResponse<CsrfToken>>> csrfToken(HttpServletRequest request) {
        return Mono.justOrEmpty((CsrfToken) request.getAttribute(CsrfToken.class.getName()))
                .flatMap(csrfToken -> successResponse("CSRF token retrieved successfully", HttpStatus.OK, csrfToken))
                .switchIfEmpty(Mono.defer(() -> errorResponse(new RuntimeException("CSRF token not found"))))
                .onErrorResume(UserManagementUtils::errorResponse);
    }
}
