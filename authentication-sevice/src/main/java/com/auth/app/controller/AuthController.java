package com.auth.app.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.auth.app.dto.AuthResponse;
import com.auth.app.dto.LoginRequest;
import com.auth.app.dto.RegisterRequest;
import com.auth.app.model.User;
import com.auth.app.repository.UserRepository;
import com.auth.app.security.jwt.JwtTokenProvider;

import java.util.List;

/**
 * Auth Controller — base path: /api/v1/auth
 *
 * Endpoints:
 *   POST /register  — Create a new account; returns JWT immediately
 *   POST /login     — Authenticate; returns JWT
 *
 * These endpoints are PUBLIC (no JWT required at the Gateway or here).
 * The API Gateway routes /api/v1/auth/** directly to this service.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtTokenProvider      tokenProvider;

    // ── POST /register ─────────────────────────────────────────────────────

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates an account and returns a JWT immediately.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {

        // Uniqueness checks
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Username '" + req.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Email '" + req.getEmail() + "' is already registered");
        }

        // Persist new user
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .name(req.getName())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(List.of("USER"))
                .active(true)
                .oauth2User(false)
                .build();

        userRepository.save(user);
        log.info("Registered new user: {}", user.getUsername());

        // Issue JWT — client is immediately authenticated
        String token = tokenProvider.generateToken(user.getUsername());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getUsername(), user.getEmail()));
    }

    // ── POST /login ──────────────────────────────────────────────────────────

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates user credentials and returns a JWT.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {

        // Delegate to Spring Security's AuthenticationManager
        // This calls CustomUserDetailsService → verifies BCrypt password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "User not found after authentication"));

        log.info("User '{}' logged in", user.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
    }
}
