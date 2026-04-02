package com.quantity.app.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user info returned by auth-service.
 * Only contains fields that measurement-service actually needs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
    private Long   id;
    private String username;
    private String email;
}
