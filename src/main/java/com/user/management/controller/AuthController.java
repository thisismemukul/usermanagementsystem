package com.user.management.controller;


import com.user.management.request.LoginRequest;
import com.user.management.request.SignupRequest;
import com.user.management.response.ApiResponse;
import com.user.management.response.LoginResponse;
import com.user.management.response.SignupResponse;
import com.user.management.response.UserInfoResponse;
import com.user.management.services.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.user.management.constants.RESTUriConstants.*;
import static com.user.management.util.UserManagementUtils.handleResponse;

@RestController
@RequestMapping(API_AUTH)
public class AuthController {

    private final IAuthService authService;

    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }


    @PostMapping(PUBLIC + SIGN_IN)
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return handleResponse(() ->
                        authService.signIn(loginRequest),
                "Authentication success",
                HttpStatus.OK);
    }

    @PostMapping(PUBLIC + SIGN_UP)
    public ResponseEntity<ApiResponse<SignupResponse>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return handleResponse(() ->
                        authService.signUp(signUpRequest),
                "User Created Successfully",
                HttpStatus.CREATED);
    }


    @GetMapping(USER)
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return handleResponse(() ->
                        authService.getUserDetails(userDetails),
                "User details fetched successfully",
                HttpStatus.OK);
    }

    @GetMapping(USERNAME)
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }
}
