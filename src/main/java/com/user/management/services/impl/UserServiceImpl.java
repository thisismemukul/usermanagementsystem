package com.user.management.services.impl;

import com.user.management.enums.AppRole;
import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.repositories.RoleRepository;
import com.user.management.repositories.UserRepository;
import com.user.management.request.dto.UserDTO;
import com.user.management.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return convertToDto(user);
    }

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
}
