package com.user.management.services;

import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.request.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);

    User findByUsername(String username);

    void updateAccountLockStatus(Long userId, boolean lock);

    List<Role> getAllRoles();

    void updateAccountExpiryStatus(Long userId, boolean expire);

    void updateAccountEnabledStatus(Long userId, boolean enabled);

    void updateCredentialsExpiryStatus(Long userId, boolean expire);

    void updatePassword(Long userId, String password);

    Optional<User> findByEmail(String email);
}
