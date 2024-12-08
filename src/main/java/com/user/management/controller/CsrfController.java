package com.user.management.controller;

import  com.user.management.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.user.management.constants.RESTUriConstants.API;
import static com.user.management.constants.RESTUriConstants.CSRF_TOKEN;
import static com.user.management.util.UserManagementUtils.errorResponse;
import static com.user.management.util.UserManagementUtils.successResponse;

@RestController
@RequestMapping(API)
public class CsrfController {

    /**
     * Retrieves the CSRF token for the current session.
     * The method fetches the CSRF token from the HTTP request attributes and returns it in the response.
     * If the CSRF token is not found, an error response is returned.
     *
     * @param request The HTTP request containing the CSRF token as an attribute.
     * @return ResponseEntity containing an ApiResponse with the CSRF token and a success message if found,
     *         or an error response if the token is not available.
     * @throws RuntimeException Thrown for any unexpected error during the retrieval process.
     */
    @GetMapping(CSRF_TOKEN)
    public ResponseEntity<ApiResponse<CsrfToken>> csrfToken(HttpServletRequest request) {
        return Optional.ofNullable((CsrfToken) request.getAttribute(CsrfToken.class.getName()))
                .map(csrfToken -> successResponse("CSRF token retrieved successfully", HttpStatus.OK, csrfToken))
                .orElseGet(() -> errorResponse(new RuntimeException("CSRF token not found")));
    }
}
