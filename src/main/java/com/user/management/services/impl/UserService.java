package com.user.management.services.impl;

import com.user.management.enums.AppRole;
import com.user.management.exceptions.UserMgmtException;
import com.user.management.exceptions.ValidationException;
import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.repositories.RoleRepository;
import com.user.management.repositories.UserRepository;
import com.user.management.request.dto.UserDTO;
import com.user.management.services.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

import static com.user.management.enums.ResponseCode.*;
import static com.user.management.util.UserManagementUtils.createUserMgmtException;
import static com.user.management.util.UserManagementUtils.createValidationException;

@Service
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
            user.setPassword(passwordEncoder.encode(password));
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

}
