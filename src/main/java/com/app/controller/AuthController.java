package com.app.controller;

import com.app.dto.auth.AuthResponse;
import com.app.dto.auth.LoginRequest;
import com.app.dto.auth.RegisterRequest;
import com.app.model.User;
import com.app.repository.UserRepository;
import com.app.security.JWT.JwtTokenProvider;

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

import java.util.List;

/**
 * REST controller for local (username/password) authentication.
 *
 * <p>Base path: {@code /api/v1/auth}
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /register – Create a new local account; returns a JWT on success.</li>
 *   <li>POST /login    – Authenticate and receive a JWT.</li>
 * </ul>
 *
 * <p>Validation is handled by {@code @Valid} + Bean Validation; errors surface
 * through {@link com.app.exception.GlobalExceptionHandler}.
 *
 * <p>Passwords are never stored in plain text – BCrypt encoding is applied
 * before persistence.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, and obtain JWT tokens")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtTokenProvider      tokenProvider;

    // ── Register ─────────────────────────────────────────────────────────────

    /**
     * Registers a new local user account.
     *
     * <p>Steps:
     * <ol>
     *   <li>Validate that username and email are not already taken (409 CONFLICT if so).</li>
     *   <li>Build and persist a new {@link User} with a BCrypt-encoded password.</li>
     *   <li>Generate and return a JWT so the client is immediately authenticated.</li>
     * </ol>
     *
     * @param req Validated registration payload.
     * @return 201 Created with {@link AuthResponse} containing the JWT.
     */
    @PostMapping("/register")
    @Operation(
        summary     = "Register a new user",
        description = "Creates a local account and returns a JWT immediately."
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {

        // ── Uniqueness checks ────────────────────────────────────────────────
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Username '" + req.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Email '" + req.getEmail() + "' is already registered");
        }

        // ── Create and persist user ─────────────────────────────────────────
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

        // ── Issue JWT ────────────────────────────────────────────────────────
        String token = tokenProvider.generateToken(user.getUsername());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getUsername(), user.getEmail()));
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    /**
     * Authenticates an existing user with username + password.
     *
     * <p>Spring Security's {@link AuthenticationManager} delegates to
     * {@link com.app.security.CustomUserDetailsService} which loads the user
     * from the database and verifies the BCrypt-encoded password.
     *
     * @param req Validated login payload.
     * @return 200 OK with {@link AuthResponse} containing the JWT.
     */
    @PostMapping("/login")
    @Operation(
        summary     = "Authenticate user",
        description = "Returns a JWT on successful authentication."
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {

        // Authenticate; throws AuthenticationException (→ 401) on failure
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        String token = tokenProvider.generateToken(authentication);

        // Load user to populate the response body
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "User not found after successful authentication"));

        log.info("User '{}' logged in successfully", req.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(token, user.getUsername(), user.getEmail()));
    }
}
