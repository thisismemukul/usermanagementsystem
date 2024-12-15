package com.user.management.security.jwt;

import com.user.management.security.TokenBlacklist;
import com.user.management.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.user.management.enums.ResponseCode.AUTHENTICATION_FAILED;
import static com.user.management.util.UserManagementUtils.createUserMgmtException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
        try {
            // Extract JWT token from the request header
            String jwt = parseJwt(request);

            if (jwt != null) {
                if (tokenBlacklist.isBlacklisted(jwt)) {
                    logger.warn("Blocked request with blacklisted token: {}", jwt);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
                    return;
                }
                if (jwtUtils.validateJwtToken(jwt)) {
                    // Get username from the token
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);

                    // Load user details from the database
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Create an authentication object with user details and roles
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    logger.debug("Roles from JWT: {}", userDetails.getAuthorities());

                    // Set authentication details and store in SecurityContext
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Error: Cannot set user authentication {}", e.getMessage(), e);
            throw createUserMgmtException(AUTHENTICATION_FAILED);
        }

        // Proceed to the next filter in the chain
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        // Extract JWT token from the Authorization header
        String jwt = jwtUtils.getJwtFromHeader(request);
        logger.debug("AuthTokenFilter.java: {}", jwt);
        return jwt;
    }
}
