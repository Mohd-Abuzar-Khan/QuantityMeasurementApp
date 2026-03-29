package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA entity representing an application user.
 *
 * <p>Supports both locally registered users (username + BCrypt password)
 * and OAuth2-originated users (email used as username, empty password,
 * {@code oauth2User = true}).
 *
 * <p>Schema notes:
 * <ul>
 *   <li>Unique indexes on {@code username} and {@code email} are enforced at
 *       the DB level (in addition to the application-level checks in
 *       {@link com.app.controller.AuthController}).</li>
 *   <li>Roles are stored as a simple element collection ("USER", "ADMIN").
 *       For more granular RBAC, replace with a dedicated ROLES table.</li>
 *   <li>{@code createdAt} / {@code updatedAt} are managed by JPA lifecycle
 *       callbacks – never set manually.</li>
 * </ul>
 *
 * <p><b>Deduplication note:</b> The codebase previously contained two user-model
 * classes ({@code User} and {@code UserEntity}).  They have been consolidated
 * here into one canonical entity.  {@code UserEntity} (which contained
 * stub/TODO implementations) is removed.
 */
@Entity
@Table(
    name = "USERS",
    indexes = {
        @Index(name = "IDX_USER_USERNAME", columnList = "username", unique = true),
        @Index(name = "IDX_USER_EMAIL",    columnList = "email",    unique = true)
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique login name (3–50 characters; validated in {@link com.app.dto.auth.RegisterRequest}). */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /** Unique email address. Used as username for OAuth2-only accounts. */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Optional display name supplied at registration or pulled from OAuth2 provider. */
    @Column(length = 100)
    private String name;

    /**
     * BCrypt-hashed password.
     * Set to an empty string for OAuth2-only accounts (they authenticate via the provider).
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * Application roles stored as a simple element collection.
     * Default value is {@code ["USER"]}.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles;

    /** {@code false} = account is disabled (soft-delete pattern). */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** {@code true} when the account was created via an OAuth2 provider (e.g. Google). */
    @Column(nullable = false)
    @Builder.Default
    private boolean oauth2User = false;

    /** Set once on first persist; never updated. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Updated on every modification via the JPA lifecycle callback. */
    @Column
    private LocalDateTime updatedAt;

    // ── JPA lifecycle callbacks ───────────────────────────────────────────────

    /** Automatically stamped when the entity is first persisted. */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    /** Automatically updated whenever the entity is modified and re-persisted. */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
