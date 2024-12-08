package com.user.management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter to validate incoming HTTP requests based on a custom header.
 * This filter checks for the presence and correctness of the "X-Valid-Password-Bol" header
 * and blocks the request if validation fails.
 *
 * <p>Extends {@link OncePerRequestFilter} to ensure the filter is executed once per request.
 */
@Component
public class RequestValidationFilter extends OncePerRequestFilter {

    /**
     * Filters incoming requests to validate the "X-Valid-Password-Bol" header.
     * If the header is absent or does not match the expected value ("myPass"),
     * the filter responds with a 400 Bad Request status and halts further processing.
     *
     * If the header is valid, the filter passes the request down the filter chain.
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the filter chain to pass the request along if valid
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException      if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Retrieve the custom header "X-Valid-Password-Bol"
        String header = request.getHeader("X-Valid-Password-Bol");

        // Validate the header value
        if (header == null || !header.equals("myPass")) {
            // Send a 400 Bad Request response if validation fails
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        // Pass the request to the next filter in the chain if valid
        filterChain.doFilter(request, response);
    }
}
