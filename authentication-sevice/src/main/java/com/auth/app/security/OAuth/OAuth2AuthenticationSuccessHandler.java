package com.auth.app.security.OAuth;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth.app.model.User;
import com.auth.app.repository.UserRepository;
import com.auth.app.security.jwt.JwtTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/callback}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        String login = resolveLogin(provider, oauth2User);
        String email = resolveEmail(provider, oauth2User, login);
        String name = resolveName(oauth2User, login);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String usernameSeed = login;
            String username = userRepository.existsByUsername(usernameSeed)
                    ? usernameSeed + "_" + System.currentTimeMillis()
                    : usernameSeed;

            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .name(name)
                    .password("")
                    .roles(List.of("USER"))
                    .active(true)
                    .oauth2User(true)
                    .build();
            return userRepository.save(newUser);
        });

        log.info("OAuth2 login success for provider={} user={}", provider, user.getUsername());

        String token = tokenProvider.generateToken(user.getUsername());
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUri)
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String resolveLogin(String provider, OAuth2User oauth2User) {
        if ("github".equalsIgnoreCase(provider)) {
            String login = oauth2User.getAttribute("login");
            return hasText(login) ? login : oauth2User.getName();
        }

        String email = oauth2User.getAttribute("email");
        if (hasText(email)) {
            int atIndex = email.indexOf('@');
            return atIndex > 0 ? email.substring(0, atIndex) : email;
        }

        return oauth2User.getName();
    }

    private String resolveEmail(String provider, OAuth2User oauth2User, String login) {
        String email = oauth2User.getAttribute("email");
        if (hasText(email)) {
            return email;
        }
        if ("github".equalsIgnoreCase(provider)) {
            return login + "@users.noreply.github.com";
        }
        return login + "@oauth.local";
    }

    private String resolveName(OAuth2User oauth2User, String fallback) {
        String name = oauth2User.getAttribute("name");
        return hasText(name) ? name : fallback;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
