package com.app.security;

import com.app.model.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bridges Spring Security's authentication infrastructure with the application's
 * {@link UserRepository}.
 *
 * <p>Spring Security calls {@link #loadUserByUsername} during the login flow (and
 * during JWT filter authentication).  This service loads the {@link User} entity,
 * checks that the account is active, and wraps its roles as Spring Security
 * {@link SimpleGrantedAuthority} objects (prefixed with "ROLE_" per Spring convention).
 *
 * <p>The class is annotated {@code @Transactional(readOnly = true)} to avoid
 * unnecessary write-locks when only reading user data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by username for Spring Security's authentication pipeline.
     *
     * @param username The username submitted during login.
     * @return A fully populated {@link UserDetails} object.
     * @throws UsernameNotFoundException If no user with that username exists or the account is inactive.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Authentication attempted for unknown user: '{}'", username);
                    return new UsernameNotFoundException(
                            "User not found with username: " + username);
                });

        if (!user.isActive()) {
            log.warn("Authentication attempted for inactive account: '{}'", username);
            throw new UsernameNotFoundException("Account is inactive: " + username);
        }

        // Map application role strings to Spring Security GrantedAuthority objects.
        // Prefix "ROLE_" is added so that @PreAuthorize("hasRole('USER')") works correctly.
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        log.debug("Loaded UserDetails for '{}' with roles {}", username, user.getRoles());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}
