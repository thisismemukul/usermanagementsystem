package com.user.management.services.impl;

import com.user.management.enums.AppRole;
import com.user.management.exceptions.UserMgmtException;
import com.user.management.exceptions.ValidationException;
import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.repositories.RoleRepository;
import com.user.management.repositories.UserRepository;
import com.user.management.request.LoginRequest;
import com.user.management.request.SignupRequest;
import com.user.management.response.LoginResponse;
import com.user.management.response.SignupResponse;
import com.user.management.response.UserInfoResponse;
import com.user.management.security.jwt.JwtUtils;
import com.user.management.security.services.UserDetailsImpl;
import com.user.management.services.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.user.management.constants.Constants.ADMIN;
import static com.user.management.enums.ResponseCode.*;
import static com.user.management.util.UserManagementUtils.*;

@Service
@Slf4j
public class AuthService implements IAuthService {


    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final UserService userService;

    public AuthService(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.userService = userService;
    }

    /**
     * Authenticates a user and generates a JWT token if the credentials are valid.
     * The method validates the input request, performs authentication,
     * and retrieves the user's roles and a JWT token upon successful login.
     * It also handles exceptions for input validation and authentication failures.
     *
     * @param loginRequest The login request containing username and password to authenticate.
     * @return LoginResponse containing the authenticated username, roles, and JWT token.
     * @throws ValidationException Thrown if the input validation fails (e.g., empty or invalid fields).
     * @throws UserMgmtException Thrown if the authentication process fails.
     */
    @Override
    public LoginResponse signIn(LoginRequest loginRequest) {

        if (ObjectUtils.isEmpty(loginRequest)
                || ObjectUtils.isEmpty(loginRequest.getUsername())
                || ObjectUtils.isEmpty(loginRequest.getPassword())) {
            throw createValidationException(INPUT_IS_INVALID); //TODO: either username and pass are invalid (check for password length etc ) in @validate annotation
        }
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            log.info("Validating userDetails");
            validateUserDetails(userDetails);
            String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            return new LoginResponse(
                    userDetails.getUsername(),
                    roles, jwtToken);

        } catch (AuthenticationException e) {
            log.error("Error: AuthenticationException {}", e.getMessage(), e);
            throw createUserMgmtException(AUTHENTICATION_FAILED);
        }
    }

    /**
     * Registers a new user account with the provided signup details.
     * The method validates the input request, checks for username and email availability,
     * assigns appropriate roles, and saves the new user to the database.
     * If any validation or persistence step fails, an appropriate exception is thrown.
     *
     * @param signupRequest The request containing username, email, password, and role information for the new user.
     * @return SignupResponse containing the newly created user ID, username, and assigned role.
     * @throws ValidationException Thrown if the input validation fails (e.g., empty or invalid fields).
     * @throws UserMgmtException Thrown if the username or email is already in use, or the role is not found.
     * @throws RuntimeException Thrown for any unexpected error during the signup process.
     */

    @Override
    public SignupResponse signUp(SignupRequest signupRequest) {

        if (ObjectUtils.isEmpty(signupRequest)
                || ObjectUtils.isEmpty(signupRequest.getUsername())
                || ObjectUtils.isEmpty(signupRequest.getEmail())
                || ObjectUtils.isEmpty(signupRequest.getRole())
                || ObjectUtils.isEmpty(signupRequest.getPassword())) {
            throw createValidationException(INPUT_IS_INVALID); //TODO: either username and pass are invalid (check for password length etc ) in @validate annotation
        }
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            log.error("Error: Username {} is already taken!", signupRequest.getUsername());
            throw createUserMgmtException(USERNAME_ALREADY_TAKEN);
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            log.error("Error: Email {} is already in use!", signupRequest.getEmail());
            throw createUserMgmtException(EMAIL_ALREADY_TAKEN);
        }
        try {
            User user = new User(
                    signupRequest.getUsername(),
                    signupRequest.getEmail(),
                    encoder.encode(signupRequest.getPassword()));

            Set<String> strRoles = signupRequest.getRole();
            Role role;
            if (strRoles == null || strRoles.isEmpty()) {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> createUserMgmtException(ROLE_NOT_FOUND));
            } else {
                String roleStr = strRoles.iterator().next();
                if (roleStr.equals(ADMIN)) {
                    role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                            .orElseThrow(() -> createUserMgmtException(ROLE_NOT_FOUND));
                } else {
                    role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                            .orElseThrow(() -> createUserMgmtException(ROLE_NOT_FOUND));
                }

                user.setAccountNonLocked(true);
                user.setAccountNonExpired(true);
                user.setCredentialsNonExpired(true);
                user.setEnabled(true);
                user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                user.setAccountExpiryDate(LocalDate.now().plusYears(1));
                user.setTwoFactorEnabled(false);
                user.setSignUpMethod("email");
            }
            user.setRole(role);
            User save = userRepository.save(user);
            return new SignupResponse(
                    save.getId(),
                    signupRequest.getUsername(),
                    save.getRole());

        } catch (RuntimeException e) {
            log.error("Error: while sign up {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves detailed information about an authenticated user.
     * The method validates the user details object, fetches the user record from the database,
     * and returns a response containing user attributes and roles.
     *
     * @param userDetails The authenticated user details object to validate and use for fetching user information.
     * @return UserInfoResponse containing the user's details, account status, and roles.
     * @throws ValidationException Thrown if the user details object is invalid.
     * @throws RuntimeException Thrown for any unexpected error during the retrieval process.
     */
    @Override
    public UserInfoResponse getUserDetails(UserDetails userDetails) {
        validateUserDetails(userDetails);
        try {
            User user = userService.findByUsername(userDetails.getUsername());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return new UserInfoResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.isAccountNonLocked(),
                    user.isAccountNonExpired(),
                    user.isCredentialsNonExpired(),
                    user.isEnabled(),
                    user.getCredentialsExpiryDate(),
                    user.getAccountExpiryDate(),
                    user.isTwoFactorEnabled(),
                    roles
            );
        } catch (Exception e) {
            log.error("Error: while fetching user info {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
