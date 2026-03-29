package com.app.security.OAuth;

import com.app.model.User;
import com.app.repository.UserRepository;
import com.app.security.JWT.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Handles a successful OAuth2 login (e.g. Google).
 *
 * <h3>Flow:</h3>
 * <ol>
 *   <li>Extract the {@code email} and {@code name} attributes from the OAuth2 principal.</li>
 *   <li>Upsert the user in the local database:
 *       <ul>
 *         <li>First login → create a new {@link User} with {@code oauth2User = true}.</li>
 *         <li>Subsequent logins → update the display name (email never changes).</li>
 *       </ul>
 *   </li>
 *   <li>Issue a JWT for the user.</li>
 *   <li>Redirect to the configured frontend callback URL with the token as a
 *       query parameter ({@code ?token=...}).</li>
 * </ol>
 *
 * <h3>Configuration (application.properties):</h3>
 * <pre>
 *   app.oauth2.redirect-uri=http://localhost:4200/oauth2/callback
 * </pre>
 *
 * <p><b>Security note:</b> In production, validate that the redirect URI matches
 * a whitelist before redirecting to prevent open-redirect attacks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository   userRepository;

    /**
     * Frontend URI to redirect to after a successful OAuth2 login.
     * Override in production via {@code app.oauth2.redirect-uri}.
     */
    @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/callback}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest  request,
                                        HttpServletResponse response,
                                        Authentication      authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Both attributes are guaranteed by the google OAuth2 scope (email,profile)
        String email = oauth2User.getAttribute("email");
        String name  = oauth2User.getAttribute("name");

        if (email == null) {
            log.error("OAuth2 user has no email attribute; cannot proceed");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                               "OAuth2 provider did not return an email address");
            return;
        }

        // ── Upsert user ───────────────────────────────────────────────────────
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            log.info("First OAuth2 login for {}; creating new user record", email);
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email);      // email is used as the username for OAuth2 accounts
            newUser.setPassword("");         // no local password
            newUser.setRoles(List.of("USER"));
            newUser.setActive(true);
            newUser.setOauth2User(true);
            return newUser;
        });

        // Always sync the display name from the provider
        user.setName(name);
        userRepository.save(user);

        // ── Issue JWT ─────────────────────────────────────────────────────────
        String token = tokenProvider.generateToken(email);

        String redirectUri = UriComponentsBuilder
                .fromUriString(frontendRedirectUri)
                .queryParam("token", token)
                .build()
                .toUriString();

        log.info("OAuth2 login successful for {}; redirecting to {}", email, frontendRedirectUri);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
