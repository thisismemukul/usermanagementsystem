package com.user.management.services;


import com.user.management.models.User;
import com.user.management.request.dto.UserDTO;

import java.util.List;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);

    User findByUsername(String username);
}
