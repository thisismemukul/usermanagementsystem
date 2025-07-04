package com.user.management.services.impl;

import com.user.management.enums.AppRole;
import com.user.management.exceptions.UserMgmtException;
import com.user.management.exceptions.ValidationException;
import com.user.management.models.PasswordReset;
import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.repositories.PasswordResetRepository;
import com.user.management.repositories.RoleRepository;
import com.user.management.repositories.UserRepository;
import com.user.management.request.dto.UserDTO;
import com.user.management.services.ITotpService;
import com.user.management.services.IUserService;
import com.user.management.util.EmailService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.user.management.constants.Constants.EMAIL;
import static com.user.management.constants.RESTUriConstants.RESET_PASSWORD;
import static com.user.management.enums.ResponseCode.*;
import static com.user.management.util.UserManagementUtils.createUserMgmtException;
import static com.user.management.util.UserManagementUtils.createValidationException;

@Service
@Slf4j
public class UserService implements IUserService {


    @Value("${frontend.url}")
    private String frontendUrl;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordResetRepository passwordResetRepository;

    private final EmailService emailService;

    private final ITotpService totpService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordResetRepository passwordResetRepository, EmailService emailService, ITotpService totpService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.emailService = emailService;
        this.totpService = totpService;
    }

    /**
     * Updates the role for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userId   The userId to first validate and used to find user.
     * @param roleName The roleName to first validate and to find by role name.
     * @throws ValidationException Thrown if failed to validate input.
     * @throws ServiceException    Thrown if failed to create note.
     */
    @Override
    public void updateUserRole(Long userId, String roleName) {
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(roleName)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            AppRole appRole = AppRole.valueOf(roleName);
            Role role = roleRepository.findByRoleName(appRole)
                    .orElseThrow(() -> createUserMgmtException(ROLE_NOT_FOUND));
            user.setRole(role);
            userRepository.save(user);
        } catch (RuntimeException e) {
            log.error("Error occurred while updating role: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update role", e);
        }
    }


    /**
     * To fetch all the users, throwing an appropriate exception if something went wrong.
     *
     * @throws ServiceException Thrown if failed to fetch users.
     */
    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while fetching all users: {}", e.getMessage(), e);
            throw new ServiceException("Failed to fetch all users", e);
        }
    }

    /**
     * To fetch the user by id, throwing an appropriate exception if something went wrong.
     *
     * @throws ServiceException Thrown if failed to fetch users.
     */
    @Override
    public UserDTO getUserById(Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow();
            return convertToDto(user);
        } catch (Exception e) {
            log.error("Error occurred while fetching user: {}", e.getMessage(), e);
            throw new ServiceException("Failed to fetch user", e);
        }
    }

    /**
     * To convertToDto from User to UserDTO.
     */
    private UserDTO convertToDto(User user) {
        return UserDTO.builder()
                .userId(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .accountNonLocked(user.isAccountNonLocked())
                .accountNonExpired(user.isAccountNonExpired())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .enabled(user.isEnabled())
                .credentialsExpiryDate(user.getCredentialsExpiryDate())
                .accountExpiryDate(user.getAccountExpiryDate())
                .twoFactorSecret(user.getTwoFactorSecret())
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .signUpMethod(user.getSignUpMethod())
                .role(user.getRole())
                .createdDate(user.getCreatedAt())
                .updatedDate(user.getUpdatedAt())
                .build();
    }

    /**
     * Get the user by username, throwing an appropriate exception if something went wrong.
     *
     * @param username The username to first validate and used to find user.
     * @throws UserMgmtException Thrown if username not found.
     */
    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> {
            log.error("Username {} not found", username);
            return createUserMgmtException(USERNAME_NOT_FOUND);
        });
    }

    /**
     * Updates the account lock status for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userId The userId to first validate and used to find user.
     * @param lock   The lock to first validate and to handle lock status.
     * @throws ValidationException Thrown if failed to validate input.
     * @throws ServiceException    Thrown if failed to create note.
     */
    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(lock)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            User user = userRepository.findById(userId).orElseThrow(()
                    -> createUserMgmtException(USERNAME_NOT_FOUND));
            user.setAccountNonLocked(!lock);
            userRepository.save(user);
        } catch (RuntimeException e) {
            log.error("Error occurred while updating account lock status: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update account lock status", e);
        }
    }


    /**
     * To fetch all the users, throwing an appropriate exception if something went wrong.
     *
     * @throws ServiceException Thrown if failed to fetch users.
     */
    @Override
    public List<Role> getAllRoles() {
        try {
            return roleRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while fetching all roles: {}", e.getMessage(), e);
            throw new ServiceException("Failed to fetch all roles", e);
        }
    }

    /**
     * Updates the account expiry status for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userId The userId to first validate and used to find user.
     * @param expire The expiry to first validate and to handle expire status.
     * @throws ValidationException Thrown if failed to validate input.
     * @throws ServiceException    Thrown if failed to create note.
     */
    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(expire)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            User user = userRepository.findById(userId).orElseThrow(()
                    -> createUserMgmtException(USERNAME_NOT_FOUND));
            user.setAccountNonExpired(!expire);
            userRepository.save(user);
        } catch (RuntimeException e) {
            log.error("Error occurred while updating account expiry status: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update account expiry status", e);
        }
    }

    /**
     * Updates the account enabled status for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userId  The userId to first validate and used to find user.
     * @param enabled The enabled to first validate and to handle enabled status.
     * @throws ValidationException Thrown if failed to validate input.
     * @throws ServiceException    Thrown if failed to create note.
     */
    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(enabled)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            User user = userRepository.findById(userId).orElseThrow(()
                    -> createUserMgmtException(USERNAME_NOT_FOUND));
            user.setEnabled(enabled);
            userRepository.save(user);
        } catch (RuntimeException e) {
            log.error("Error occurred while updating account enabled status: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update account enabled status", e);
        }
    }

    /**
     * Updates the Credentials expire status for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userId The userId to first validate and used to find user.
     * @param expire The expiry to first validate and to handle credentials expire status.
     * @throws ValidationException Thrown if failed to validate input.
     * @throws ServiceException    Thrown if failed to create note.
     */
    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(expire)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            User user = userRepository.findById(userId).orElseThrow(()
                    -> createUserMgmtException(USERNAME_NOT_FOUND));
            user.setCredentialsNonExpired(!expire);
            userRepository.save(user);
        } catch (RuntimeException e) {
            log.error("Error occurred while updating Credentials expire status: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update Credentials expire status", e);
        }
    }


    /**
     * Updates the Credentials expire status for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userId   The userId to first validate and used to find user.
     * @param password The password to first validate and to encode and save.
     * @throws ValidationException Thrown if failed to validate input.
     * @throws ServiceException    Thrown if failed to create note.
     */
    @Override
    public void updatePassword(Long userId, String password) {
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(password)) {
            throw createValidationException(INPUT_IS_INVALID);
        }
        try {
            User user = userRepository.findById(userId).orElseThrow(()
                    -> createUserMgmtException(USERNAME_NOT_FOUND));
            user.setPassword(password);
            userRepository.save(user);
        } catch (RuntimeException e) {
            log.error("Error occurred while updating password: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update password", e);
        }
    }

    /**
     * To find by email ID, throwing an appropriate exception if something went wrong.
     *
     * @throws ServiceException Thrown if failed to fetch users.
     */

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            log.error("Error occurred while finding user with email: {}", e.getMessage(), e);
            throw new ServiceException("Failed to find by email.", e);
        }
    }

    /**
     * Generates a password reset token for a user, validates the email, and persists the token in the database.
     *
     * @param email The email of the user for whom the password reset token will be generated.
     * @throws ValidationException Thrown if the email is invalid or not provided.
     * @throws ServiceException    Thrown if there is an issue generating or saving the password reset token.
     */
    @Override
    public void generatePasswordResetToken(String email){
        if (ObjectUtils.isEmpty(email)) {
            log.error("Invalid input: Email is null or empty");
            throw createValidationException(INPUT_IS_INVALID);
        }

        try {
            User user = findByEmail(email).orElseThrow(() -> {
                log.error("User not found for email: {}", email);
                return createUserMgmtException(USER_NOT_FOUND);
            });

            String token = UUID.randomUUID().toString();
            Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);

            PasswordReset passwordReset = PasswordReset.builder()
                    .token(token)
                    .expiryDate(expiryDate)
//                    .used(false)
                    .user(user)
                    .build();

            passwordResetRepository.save(passwordReset);

            log.info("Password reset token generated and saved successfully for email: {}", email);

            String resetUrl = frontendUrl+RESET_PASSWORD+"?token="+token;
            emailService.sendEmail(user.getEmail(), "Password Reset Request", resetUrl);

        } catch (RuntimeException e) {
            log.error("Error occurred while generating password reset token for email {}: {}", email, e.getMessage(), e);
            throw new ServiceException("Failed to generate password reset token.", e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resets the password for a user based on a valid password reset token.
     * Validates the token, updates the user's password, and marks the token as used.
     *
     * @param token       The password reset token provided by the user.
     * @param newPassword The new password to be set for the user.
     * @throws ServiceException If the token is invalid, expired, already used, or does not reference a valid user.
     */
    @Override
    public void resetPassword(String token, String newPassword) {
        try {
            PasswordReset passwordResetToken = passwordResetRepository.findByToken(token)
                    .orElseThrow(() -> new ServiceException("Invalid or missing password reset token."));

            if (passwordResetToken.isUsed()) {
                log.warn("Attempt to use an already used password reset token: {}", token);
                throw new ServiceException("This password reset token has already been used. Please request a new one.");
            }

            if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
                log.warn("Attempt to use an expired password reset token: {}", token);
                throw new ServiceException("This password reset token has expired. Please request a new one.");
            }

            User user = passwordResetToken.getUser();

            if (ObjectUtils.isEmpty(user)) {
                log.error("Password reset token {} does not reference a valid user.", token);
                throw createUserMgmtException(USER_NOT_FOUND);
            }

            user.setPassword(newPassword);
            userRepository.save(user);
            log.info("Password successfully reset for user: {}", user.getEmail());

            passwordResetToken.setUsed(true);
            passwordResetRepository.save(passwordResetToken);
            log.info("Password reset token marked as used for user: {}", user.getEmail());
        } catch (ServiceException e) {
            log.error("ServiceException while resetting password: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while resetting password for token: {}", token, e);
            throw new ServiceException("An unexpected error occurred while resetting the password. Please try again.");
        }
    }


    @Override
    public void registerUser(User newUser) {
        if (ObjectUtils.isEmpty(newUser)) {
            throw createUserMgmtException(USER_NOT_FOUND);
        }

        if (!ObjectUtils.isEmpty(newUser.getSignUpMethod())) {
            if (!EMAIL.equalsIgnoreCase(newUser.getSignUpMethod())) {
                newUser.setPassword(newUser.getSignUpMethod().concat(newUser.getUsername()));
            }
        }
        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            log.error("Error: while registering user {}", e.getMessage(), e);
            throw new ServiceException("An unexpected error occurred while resetting the password. Please try again.");
        }
    }

    @Override
    public GoogleAuthenticatorKey generate2FASecret(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()
                -> createUserMgmtException(USERNAME_NOT_FOUND));

        GoogleAuthenticatorKey googleAuthenticatorKey = totpService.generateSecretKey();
        user.setTwoFactorSecret(googleAuthenticatorKey.getKey());
        userRepository.save(user);
        return googleAuthenticatorKey;
    }

    @Override
    public boolean validate2FACode(Long userId, int code){
        User user = userRepository.findById(userId).orElseThrow(()
                -> createUserMgmtException(USERNAME_NOT_FOUND));

      return totpService.verifyCode(user.getTwoFactorSecret(), code);
    }

    @Override
    public void enable2FA(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()
                -> createUserMgmtException(USERNAME_NOT_FOUND));
        user.setTwoFactorEnabled(true);
    }

    @Override
    public void disable2FA(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()
                -> createUserMgmtException(USERNAME_NOT_FOUND));
        user.setTwoFactorEnabled(false);
    }

}
