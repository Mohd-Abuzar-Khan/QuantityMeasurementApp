package com.app.security;

import com.app.security.JWT.JwtAuthenticationFilter;
import com.app.security.OAuth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;

/**
 * Central Spring Security configuration.
 *
 * <h3>Key design decisions:</h3>
 * <ul>
 *   <li><b>Stateless sessions</b> – JWT-based; no HTTP session is ever created
 *       ({@link SessionCreationPolicy#STATELESS}).</li>
 *   <li><b>CSRF disabled</b> – Safe for a stateless REST API with JWT auth.</li>
 *   <li><b>CORS</b> – Permissive for development (localhost:4200 Angular dev server).
 *       Tighten in production via the {@code allowedOrigins} list.</li>
 *   <li><b>JWT filter</b> – Inserted before
 *       {@link UsernamePasswordAuthenticationFilter} so tokens are validated
 *       before any form-login processing.</li>
 *   <li><b>OAuth2</b> – Google login is supported; on success the
 *       {@link OAuth2AuthenticationSuccessHandler} issues a JWT and redirects.</li>
 * </ul>
 *
 * <h3>Public endpoints (no JWT required):</h3>
 * <ul>
 *   <li>POST /api/v1/auth/**  (register, login)</li>
 *   <li>GET  /swagger-ui.html, /v3/api-docs/**</li>
 *   <li>GET  /h2-console/**  (dev only)</li>
 *   <li>GET  /actuator/health</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity           // enables @PreAuthorize / @PostAuthorize on service methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService       userDetailsService;
    private final JwtAuthenticationFilter        jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    @Value("${app.frontend.origin:https://quantity-measurement-app-frontend-eta.vercel.app}")
    private String frontendOrigin;

    // ── Main filter chain ─────────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Stateless REST API – no CSRF tokens needed
            .csrf(csrf -> csrf.disable())

            // CORS (configured separately below)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Stateless session management – every request must carry a JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints are public
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                // Swagger / OpenAPI docs
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                 "/v3/api-docs/**").permitAll()
                // H2 console (development only – disable in production profile)
                .requestMatchers("/h2-console/**").permitAll()
                // Actuator health probe
                .requestMatchers("/actuator/health").permitAll()
                // Everything else requires a valid JWT
                .anyRequest().authenticated()
            )

            // Allow H2 console to render (uses iframes)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // OAuth2 login flow
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oauth2SuccessHandler)
            )

            // Register the JWT filter before Spring's form-login filter
            .addFilterBefore(jwtAuthenticationFilter,
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── CORS configuration ────────────────────────────────────────────────────

    /**
     * Permissive CORS for local development.
     * In production, replace {@code allowedOrigins} with the exact frontend URL.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            frontendOrigin,
            "http://localhost:4200",   // Angular dev server
            "http://localhost:3000"    // React/Next.js if used
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ── Authentication beans ──────────────────────────────────────────────────

    /**
     * DAO authentication provider – wires together our {@link CustomUserDetailsService}
     * and BCrypt password encoder so Spring Security can authenticate local logins.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} as a bean so controllers
     * (e.g. {@link com.app.controller.AuthController}) can inject and use it.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt password encoder (strength 10 – good balance of security vs speed).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
