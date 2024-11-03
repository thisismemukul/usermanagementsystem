package com.user.management.request;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import static com.user.management.constants.Constants.PASSWORD_IS_EMPTY_MESSAGE;
import static com.user.management.constants.Constants.USERNAME_IS_EMPTY_MESSAGE;

@Data
public class SignupRequest {
    @NotBlank(message = USERNAME_IS_EMPTY_MESSAGE)
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Setter
    @Getter
    private Set<String> role;

    @NotBlank(message = PASSWORD_IS_EMPTY_MESSAGE)
    @Size(min = 6, max = 40)
    private String password;
}