package com.branding.branding_backend.auth;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
