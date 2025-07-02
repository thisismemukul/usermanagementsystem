package com.user.management.util;


import com.user.management.models.User;
import com.user.management.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.user.management.enums.ResponseCode.USERNAME_NOT_FOUND;
import static com.user.management.util.UserManagementUtils.createUserMgmtException;

@Component
public class AuthUtil {

    private final UserRepository userRepository;

    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> createUserMgmtException(USERNAME_NOT_FOUND));
        return user.getId();
    }

    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> createUserMgmtException(USERNAME_NOT_FOUND));
    }
}

