package com.user.management.config;

import com.user.management.enums.AppRole;
import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.repositories.RoleRepository;
import com.user.management.security.jwt.JwtUtils;
import com.user.management.security.services.UserDetailsImpl;
import com.user.management.services.impl.UserService;
import com.user.management.util.UserManagementUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.user.management.constants.Constants.*;
import static com.user.management.enums.ResponseCode.ROLE_NOT_FOUND;
import static com.user.management.util.UserManagementUtils.createUserMgmtException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        try {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            Map<String, Object> attributes = getOAuthUserAttributes(authToken);

            String email = attributes.getOrDefault(EMAIL, "").toString();
            String username = resolveUsername(authToken, attributes);

            Optional<User> existingUser = userService.findByEmail(email);

            existingUser.ifPresentOrElse(
                    user -> authenticateExistingUser(user, attributes, authToken),
                    () -> authenticateNewUser(email, username, attributes, authToken)
            );

            String jwtToken = generateJwtToken(authentication, username, email);
            redirectWithToken(response, jwtToken);
        } catch (Exception ex) {
            logger.error("Error during OAuth2 authentication success handling", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
        }
    }

    private Map<String, Object> getOAuthUserAttributes(OAuth2AuthenticationToken authToken) {
        return ((DefaultOAuth2User) authToken.getPrincipal()).getAttributes();
    }

    private String resolveUsername(OAuth2AuthenticationToken authToken, Map<String, Object> attributes) {
        String clientId = authToken.getAuthorizedClientRegistrationId();
        if (GITHUB.equals(clientId)) {
            return attributes.getOrDefault("login", "").toString();
        } else if (GOOGLE.equals(clientId)) {
            String email = attributes.getOrDefault(EMAIL, "").toString();
            return email.split("@")[0];
        }
        return "";
    }

    private void authenticateExistingUser(User user, Map<String, Object> attributes, OAuth2AuthenticationToken authToken) {
        Authentication auth = createAuthenticationToken(user, attributes, authToken.getAuthorizedClientRegistrationId());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void authenticateNewUser(String email, String username, Map<String, Object> attributes, OAuth2AuthenticationToken authToken) {
        Role defaultRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> createUserMgmtException(ROLE_NOT_FOUND));

        User newUser = User.builder()
                .email(email)
                .username(username)
                .role(defaultRole)
                .signUpMethod(authToken.getAuthorizedClientRegistrationId())
                .build();

        UserManagementUtils.makeUser(newUser);
        userService.registerUser(newUser);

        Authentication auth = createAuthenticationToken(newUser, attributes, authToken.getAuthorizedClientRegistrationId());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Authentication createAuthenticationToken(User user, Map<String, Object> attributes, String clientId) {
        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                attributes,
                "id"
        );
        return new OAuth2AuthenticationToken(oauthUser, oauthUser.getAuthorities(), clientId);
    }

    private String generateJwtToken(Authentication authentication, String username, String email) {

        UserDetailsImpl userDetails = new UserDetailsImpl(
                null,
                username,
                email,
                null,
                false,
                authentication.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                        .collect(Collectors.toList())
        );
        return jwtUtils.generateTokenFromUsername(userDetails);
    }

    private void redirectWithToken(HttpServletResponse response, String jwtToken) throws IOException {
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", jwtToken)
                .build()
                .toUriString();
        setDefaultTargetUrl(targetUrl);
        getRedirectStrategy().sendRedirect(null, response, targetUrl);
    }
}
