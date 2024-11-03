package com.user.management.response;

import com.user.management.models.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponse {
    private Long id;
    private String username;
    private Role roles;

    public SignupResponse(Long id, String username, Role roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
