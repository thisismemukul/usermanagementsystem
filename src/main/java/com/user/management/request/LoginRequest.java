package com.user.management.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import static com.user.management.constants.Constants.PASSWORD_IS_EMPTY_MESSAGE;
import static com.user.management.constants.Constants.USERNAME_IS_EMPTY_MESSAGE;

@Setter
@Getter
public class LoginRequest {

    @NotEmpty(message = USERNAME_IS_EMPTY_MESSAGE)
    private String username;
    @NotEmpty(message = PASSWORD_IS_EMPTY_MESSAGE)
    private String password;
}