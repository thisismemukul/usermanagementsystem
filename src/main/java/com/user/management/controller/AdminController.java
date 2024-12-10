package com.user.management.controller;

import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.response.ApiResponse;
import com.user.management.services.IUserService;
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

    private final IUserService IUserService;

    public AdminController(IUserService IUserService) {
        this.IUserService = IUserService;
    }

    /**
     * Retrieves a list of all users.
     *
     * @return ResponseEntity with a list of users and a success message.
     */
    @GetMapping(USERS)
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return handleResponse(IUserService::getAllUsers,
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
    @PutMapping(UPDATE + ROLE)
    public ResponseEntity<ApiResponse<String>> updateUserRole(@RequestParam Long userId,
                                                              @RequestParam String roleName) {
        return handleResponse(() -> {
            IUserService.updateUserRole(userId, roleName);
            return null;
        }, "Role updated successfully", HttpStatus.OK);
    }

    /**
     * Retrieves user information based on user ID.
     *
     * @param id ID of the user.
     * @return ResponseEntity with user details.
     */
    @GetMapping(USER + ID)
    public ResponseEntity<ApiResponse<List<User>>> getUser(@PathVariable Long id) {
        return handleResponse(IUserService::getAllUsers,
                "Fetched Users successfully",
                HttpStatus.OK);
    }

    /**
     * Updates account lock status for a user.
     *
     * @param userId ID of the user.
     * @param lock   Whether the account should be locked.
     * @return ResponseEntity with a success message.
     */
    @PutMapping(UPDATE + LOCK + STATUS)
    public ResponseEntity<ApiResponse<String>> updateAccountLockStatus(@RequestParam Long userId,
                                                                       @RequestParam boolean lock) {
        return handleResponse(() -> {
            IUserService.updateAccountLockStatus(userId, lock);
            return null;
        }, "Account lock status updated successfully", HttpStatus.OK);
    }

    /**
     * Retrieves all roles.
     *
     * @return ResponseEntity with a list of roles.
     */
    @GetMapping(ROLES)
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return handleResponse(IUserService::getAllRoles,
                "Fetched roles successfully",
                HttpStatus.OK);
    }

    /**
     * Updates account expiry status for a user.
     *
     * @param userId ID of the user.
     * @param expire Whether the account should be expired.
     * @return ResponseEntity with a success message.
     */
    @PutMapping(UPDATE + EXPIRY + STATUS)
    public ResponseEntity<ApiResponse<String>> updateAccountExpiryStatus(@RequestParam Long userId,
                                                                         @RequestParam boolean expire) {
        return handleResponse(() -> {
            IUserService.updateAccountExpiryStatus(userId, expire);
            return null;
        }, "Account expiry status updated successfully", HttpStatus.OK);
    }

    /**
     * Updates account enabled status for a user.
     *
     * @param userId  ID of the user.
     * @param enabled Whether the account should be enabled.
     * @return ResponseEntity with a success message.
     */
    @PutMapping(UPDATE + ENABLED + STATUS)
    public ResponseEntity<ApiResponse<String>> updateAccountEnabledStatus(@RequestParam Long userId,
                                                                          @RequestParam boolean enabled) {
        return handleResponse(() -> {
            IUserService.updateAccountEnabledStatus(userId, enabled);
            return null;
        }, "Account enabled status updated successfully", HttpStatus.OK);
    }

    /**
     * Updates credentials expiry status for a user.
     *
     * @param userId ID of the user.
     * @param expire Whether the credentials should be expired.
     * @return ResponseEntity with a success message.
     */
    @PutMapping(UPDATE + CREDENTIALS + EXPIRY + STATUS)
    public ResponseEntity<ApiResponse<String>> updateCredentialsExpiryStatus(@RequestParam Long userId,
                                                                             @RequestParam boolean expire) {
        return handleResponse(() -> {
            IUserService.updateCredentialsExpiryStatus(userId, expire);
            return null;
        }, "Credentials expiry status updated successfully", HttpStatus.OK);
    }

    /**
     * Updates the password for a user.
     *
     * @param userId   ID of the user.
     * @param password New password for the user.
     * @return ResponseEntity with a success message.
     */
    @PutMapping(UPDATE + PASSWORD)
    public ResponseEntity<ApiResponse<String>> updatePassword(@RequestParam Long userId,
                                                              @RequestParam String password) {
        return handleResponse(() -> {
            IUserService.updatePassword(userId, password);
            return null;
        }, "Password updated successfully", HttpStatus.OK);
    }
}