package com.user.management.services;


import com.user.management.request.LoginRequest;
import com.user.management.request.SignupRequest;
import com.user.management.response.LoginResponse;
import com.user.management.response.SignupResponse;
import com.user.management.response.UserInfoResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthService {
    LoginResponse signIn(LoginRequest loginRequest);

    SignupResponse signUp(SignupRequest signupRequest);

    UserInfoResponse getUserDetails(UserDetails userDetails);
}
