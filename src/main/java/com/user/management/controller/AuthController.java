package com.user.management.controller;


import com.user.management.models.User;
import com.user.management.request.LoginRequest;
import com.user.management.request.SignupRequest;
import com.user.management.response.ApiResponse;
import com.user.management.response.LoginResponse;
import com.user.management.response.SignupResponse;
import com.user.management.response.UserInfoResponse;
import com.user.management.security.jwt.JwtUtils;
import com.user.management.services.IAuthService;
import com.user.management.services.IUserService;
import com.user.management.services.impl.TotpService;
import com.user.management.services.impl.UserService;
import com.user.management.util.AuthUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.user.management.constants.RESTUriConstants.*;
import static com.user.management.util.UserManagementUtils.handleResponse;

@RestController
@RequestMapping(API_AUTH)
public class AuthController {

    private final IAuthService authService;
    private final IUserService userService;
    private final AuthUtil authUtil;
    private final TotpService totpService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(IAuthService authService, UserService userService, AuthUtil authUtil, TotpService totpService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.userService = userService;
        this.authUtil = authUtil;
        this.totpService = totpService;
        this.jwtUtils = jwtUtils;
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

    @PostMapping(PUBLIC + FORGOT_PASSWORD)
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam String userEmail) {
        return handleResponse(() -> {
                    userService.generatePasswordResetToken(userEmail);
                    return null;
                },
                "Reset Email Sent Successfully",
                HttpStatus.OK);
    }

    @PostMapping(PUBLIC+ RESET_PASSWORD)
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String token,
                                                             @RequestParam String newPassword) {
        return handleResponse(() -> {
                    userService.resetPassword(token,newPassword);
                    return null;
                },
                "Password Reset Successfully",
                HttpStatus.OK);
    }

    // 2FA Authentication
    @PostMapping(ENABLE_TFA)
    public ResponseEntity<String> enable2FA() {
        Long userId = authUtil.loggedInUserId();
        GoogleAuthenticatorKey secret = userService.generate2FASecret(userId);
        String qrCodeUrl = totpService.getQRCodeUrl(secret,
                userService.getUserById(userId).getUserName());
        return ResponseEntity.ok(qrCodeUrl);
    }

    @PostMapping(DISABLE_TFA)
    public ResponseEntity<String> disable2FA() {
        Long userId = authUtil.loggedInUserId();
        userService.disable2FA(userId);
        return ResponseEntity.ok("2FA disabled");
    }


    @PostMapping(VERIFY_TFA)
    public ResponseEntity<String> verify2FA(@RequestParam int code) {
        Long userId = authUtil.loggedInUserId();
        boolean isValid = userService.validate2FACode(userId, code);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid 2FA Code");
        }
        userService.enable2FA(userId);
        return ResponseEntity.ok("2FA Verified");
    }


    @GetMapping(USER+ TFA_STATUS)
    public ResponseEntity<?> get2FAStatus() {
        User user = authUtil.loggedInUser();
        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
        return ResponseEntity.ok().body(Map.of("is2faEnabled", user.isTwoFactorEnabled()));
    }


    @PostMapping(PUBLIC+VERIFY_TFA_LOGIN)
    public ResponseEntity<String> verify2FALogin(@RequestParam int code,
                                                 @RequestParam String jwtToken) {
        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        User user = userService.findByUsername(username);
        boolean isValid = userService.validate2FACode(user.getId(), code);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid 2FA Code");
        }
        return ResponseEntity.ok("2FA Verified");
    }
}
