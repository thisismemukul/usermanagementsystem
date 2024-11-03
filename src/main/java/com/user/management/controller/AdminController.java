package com.user.management.controller;

import com.user.management.models.User;
import com.user.management.response.ApiResponse;
import com.user.management.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.user.management.constants.RESTUriConstants.*;
import static com.user.management.util.UserManagementUtils.handleResponse;

@RestController
@RequestMapping(API_ADMIN)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a list of all users.
     *
     * @return ResponseEntity with a list of users and a success message.
     */
    @GetMapping(USERS)
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return handleResponse(userService::getAllUsers,
                "Fetched Users successfully",
                HttpStatus.OK);
    }

    /**
     * Updates a user's role based on user ID and role name.
     *
     * @param userId   ID of the user.
     * @param roleName New role name to be assigned.
     * @return ResponseEntity with a success message.
     */
    @PutMapping(UPDATE_ROLE)
    public ResponseEntity<ApiResponse<String>> updateUserRole(@RequestParam Long userId,
                                                              @RequestParam String roleName) {
        return handleResponse(() -> {
            userService.updateUserRole(userId, roleName);
            return null;
        }, "Keep deleted successfully", HttpStatus.OK);
    }

    /**
     * Retrieves user information based on user ID.
     *
     * @param id ID of the user.
     * @return ResponseEntity with user details.
     */
    @GetMapping(USER + ID)
    public ResponseEntity<ApiResponse<List<User>>> getUser(@PathVariable Long id) {
        return handleResponse(userService::getAllUsers,
                "Fetched Users successfully",
                HttpStatus.OK);
    }
}
