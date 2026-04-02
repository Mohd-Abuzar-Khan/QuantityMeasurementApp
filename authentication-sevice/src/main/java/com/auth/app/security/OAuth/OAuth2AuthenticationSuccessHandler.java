package com.auth.app.security.OAuth;


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

import com.auth.app.model.User;
import com.auth.app.repository.UserRepository;
import com.auth.app.security.jwt.JwtTokenProvider;

import java.io.IOException;
import java.util.List;

/**
 * OAuth2 Success Handler
 *
 * Called by Spring Security after a successful Google OAuth2 login.
 *
 * Flow:
 *   1. Extract user info from OAuth2 token (name, email)
 *   2. Upsert user in auth-service's database (create if first login)
 *   3. Issue a JWT
 *   4. Redirect to Angular frontend with the JWT as a query param
 *      → http://localhost:4200/oauth2/callback?token=<jwt>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository   userRepository;

    @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/callback}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest  request,
                                        HttpServletResponse response,
                                        Authentication      authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name  = oauth2User.getAttribute("name");

        // Upsert: if no account exists for this email, create one
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String username = email.split("@")[0]; // derive username from email
            // Ensure uniqueness (collision avoidance)
            String finalUsername = userRepository.existsByUsername(username)
                    ? username + "_" + System.currentTimeMillis() : username;

            User newUser = User.builder()
                    .username(finalUsername)
                    .email(email)
                    .name(name)
                    .password("")           // OAuth2 users have no password
                    .roles(List.of("USER"))
                    .active(true)
                    .oauth2User(true)
                    .build();
            return userRepository.save(newUser);
        });

        log.info("OAuth2 login success for user: {}", user.getUsername());

        String token       = tokenProvider.generateToken(user.getUsername());
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUri)
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
