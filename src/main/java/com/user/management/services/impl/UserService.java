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

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Updates the role for a particular user, throwing an appropriate exception if something went wrong.
     *
     * @param userId      The userId to first validate and used to find user.
     * @param roleName      The roleName to first validate and to find by role name.
     * @exception ValidationException Thrown if failed to validate input.
     * @exception ServiceException Thrown if failed to create note.
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
     * @exception ServiceException Thrown if failed to fetch users.
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
     * @exception ServiceException Thrown if failed to fetch users.
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
     * @param username      The username to first validate and used to find user.
     * @exception UserMgmtException Thrown if username not found.
     */
    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> {
            log.error("Username {} not found", username);
            return createUserMgmtException(USERNAME_NOT_FOUND);
        });
    }
}
