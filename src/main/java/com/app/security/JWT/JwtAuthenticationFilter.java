package com.app.security.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that intercepts every HTTP request, extracts a Bearer JWT from
 * the {@code Authorization} header, validates it, and populates the
 * {@link org.springframework.security.core.context.SecurityContext}.
 *
 * <p>Extends {@link OncePerRequestFilter} to guarantee it runs exactly once per
 * request, even with servlet forward/include chains.
 *
 * <h3>Filter flow:</h3>
 * <ol>
 *   <li>Extract the token from {@code Authorization: Bearer <token>}.</li>
 *   <li>If token is present and valid, load the corresponding user via
 *       {@link UserDetailsService}.</li>
 *   <li>Build a {@link UsernamePasswordAuthenticationToken} and store it in
 *       {@link SecurityContextHolder} – downstream code can then call
 *       {@code SecurityContextHolder.getContext().getAuthentication()}.</li>
 *   <li>Always call {@code filterChain.doFilter()} so the request continues.</li>
 * </ol>
 *
 * <p>Any exception during token processing is caught and logged; the request
 * proceeds unauthenticated, letting Spring Security's access rules produce the
 * appropriate 401/403 response.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX         = "Bearer ";

    private final JwtTokenProvider    tokenProvider;
    private final UserDetailsService  userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    FilterChain         filterChain)
            throws ServletException, IOException {

        try {
            String jwt = extractBearerToken(request);

            // Only proceed if a valid token is present
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String      username    = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Build the authentication token (no credentials needed at this point)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                // Attach request metadata (IP address, session ID) for audit purposes
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Place authentication into the SecurityContext for this request
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authenticated user '{}' via JWT", username);
            }

        } catch (Exception ex) {
            // Log and swallow; the request will continue as unauthenticated
            log.error("Cannot set user authentication from JWT: {}", ex.getMessage());
        }

        // Always pass the request down the chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the raw JWT string from the {@code Authorization} header.
     *
     * @param request The incoming HTTP request.
     * @return The token string (without the "Bearer " prefix), or {@code null}
     *         if the header is absent or malformed.
     */
    private String extractBearerToken(HttpServletRequest request) {
        String headerValue = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(headerValue) && headerValue.startsWith(BEARER_PREFIX)) {
            return headerValue.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
