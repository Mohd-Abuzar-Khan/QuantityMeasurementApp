package com.app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Request body for POST /api/v1/auth/login */
@Data
public class LoginRequest {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Password must not be blank")
    private String password;
}