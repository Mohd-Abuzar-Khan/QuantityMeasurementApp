package com.app.repository;

import com.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>All query methods are auto-implemented by Spring Data JPA from the
 * method name conventions – no custom JPQL is needed.
 *
 * <p><b>Deduplication note:</b> The original codebase contained four identical
 * copies of this interface.  They have been consolidated here into one
 * canonical declaration.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username The login name.
     * @return An {@link Optional} containing the user, or empty if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email address.
     * Used primarily by the OAuth2 upsert flow.
     *
     * @param email The email address.
     * @return An {@link Optional} containing the user, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given username already exists.
     * Used during registration to prevent duplicates.
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether a user with the given email already exists.
     * Used during registration to prevent duplicates.
     */
    boolean existsByEmail(String email);
}
