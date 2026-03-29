package com.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response body returned after a successful login or registration. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String  accessToken;
    private String  tokenType = "Bearer";
    private String  username;
    private String  email;

    public AuthResponse(String accessToken, String username, String email) {
        this.accessToken = accessToken;
        this.username    = username;
        this.email       = email;
    }
}