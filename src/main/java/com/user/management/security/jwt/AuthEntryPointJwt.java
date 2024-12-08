package com.user.management.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles unauthorized access attempts by responding with an appropriate error message
 * and HTTP status code. Implements {@link AuthenticationEntryPoint} to customize the behavior
 * when an unauthenticated user attempts to access a protected resource.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Commences an unauthorized response when authentication fails.
     *
     * @param request       the HTTP request
     * @param response      the HTTP response
     * @param authException the exception raised due to authentication failure
     * @throws IOException      if an I/O error occurs during response handling
     * @throws ServletException if a servlet-related error occurs during processing
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage(), authException);

        try {
            handleUnauthorizedResponse(response, authException, request);
        } catch (IOException e) {
            logger.error("Error while writing the unauthorized response: {}", e.getMessage(), e);
            throw new ServletException("Error handling unauthorized response", e);
        }
    }

    /**
     * Constructs and writes an unauthorized response body in JSON format.
     *
     * @param response      the HTTP response
     * @param authException the exception raised due to authentication failure
     * @param request       the HTTP request containing details about the unauthorized access
     * @throws IOException if an error occurs while writing to the response output stream
     */
    private void handleUnauthorizedResponse(HttpServletResponse response, AuthenticationException authException, HttpServletRequest request)
            throws IOException {
        // Set response content type and status
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Construct the response body
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        // Write the response body to the output stream
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        } catch (IOException e) {
            logger.error("Failed to write response body: {}", e.getMessage(), e);
            throw e;
        }
    }
}
