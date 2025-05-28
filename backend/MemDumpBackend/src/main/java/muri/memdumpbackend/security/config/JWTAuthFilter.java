package muri.memdumpbackend.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import muri.memdumpbackend.model.Role;
import muri.memdumpbackend.util.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        log.debug("Checking authentication for {}", uri);

        // Clear SecurityContext to prevent residual authentication
        SecurityContextHolder.clearContext();

        if (uri.startsWith("/ws-chat")) {
            processWebSocket(uri, request, response, filterChain);
            return;
        }

        if (uri.startsWith("/api/auth/login") ||
                uri.startsWith("/api/auth/register") ||
                uri.startsWith("/api/auth/mail") ||
                uri.startsWith("/api/auth/reset") ||
                uri.startsWith("/api/post/get") ||
                uri.startsWith("/api/profile/picture") ||
                uri.startsWith("/api/profile/get") ||
                uri.startsWith("/api/tag/get") ||
                method.equalsIgnoreCase("OPTIONS")) {
            log.debug("Skipping authentication for {}", uri);
            filterChain.doFilter(request, response);
            return;
        } else if (uri.startsWith("/api/auth/refresh")) {
            processRefresh(uri, request, response, filterChain);
            return;
        }
        processProtected(uri, request, response, filterChain);
    }

    private void processWebSocket(String uri, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        log.debug("Processing WebSocket upgrade request for URI: {}", uri);

        // Check for WebSocket upgrade headers
        String upgradeHeader = request.getHeader("Upgrade");
        if (!"websocket".equalsIgnoreCase(upgradeHeader)) {
            log.warn("Invalid WebSocket upgrade request for URI: {}", uri);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Not a WebSocket upgrade request");
            return;
        }

        // Extract JWT from query parameter
        String accessToken = request.getParameter("access_token");
        if (accessToken == null || accessToken.trim().isEmpty()) {
            log.warn("No access token provided in query parameter for WebSocket URI: {}", uri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Access token required for WebSocket");
            return;
        }

        try {
            if (jwtUtil.isTokenValid(accessToken)) {
                String username = jwtUtil.getSubject(accessToken);
                Role role = jwtUtil.getRole(accessToken);
                log.debug("WebSocket token valid for URI: {}. username={}, role={}", uri, username, role);
                setAuthentication(username, role);
                request.setAttribute("simpSessionAttributes", Collections.singletonMap("access_token", accessToken));
                filterChain.doFilter(request, response);
            } else {
                log.warn("Invalid WebSocket access token for URI: {}", uri);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid WebSocket access token");
            }
        } catch (Exception e) {
            log.warn("WebSocket token validation error for URI: {}", uri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid WebSocket access token");
        }
    }

    private void processProtected(String uri, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        log.debug("Processing protected endpoint: {}", uri);
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Access token is missing or invalid for URI: {}", uri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Access token required");
            return;
        }

        String accessToken = authHeader.substring(7);
        try {
            if (jwtUtil.isTokenValid(accessToken)) {
                String username = jwtUtil.getSubject(accessToken);
                Role role = jwtUtil.getRole(accessToken);
                log.debug("Access token is valid for URI: {}. username={}, role={}", uri, username, role);
                setAuthentication(username, role);
                filterChain.doFilter(request, response);
            } else {
                log.warn("Access token is invalid for URI: {}", uri);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
            }
        } catch (Exception e) {
            log.warn("Token validation error for URI: {}: {}", uri, e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token: " + e.getMessage());
        }
    }

    private void processRefresh(String uri, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        log.debug("Processing refresh token request for URI: {}", uri);
        Optional<String> refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken.isEmpty()) {
            log.warn("No refresh token found for URI: {}", uri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "No refresh token found in cookies");
            return;
        }

        String token = refreshToken.get();
        try {
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.getSubject(token);
                Role role = jwtUtil.getRole(token);
                log.info("Refresh token is valid for username: {}, role: {}", username, role);
                setAuthentication(username, role);
                filterChain.doFilter(request, response);
            } else {
                log.warn("Invalid or expired refresh token for URI: {}", uri);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token");
            }
        } catch (Exception e) {
            log.warn("Refresh token validation error: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token: " + e.getMessage());
        }
    }

    private Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }

    private void setAuthentication(String username, Role role) {
        log.debug("Setting authentication: principal={}, role=ROLE_{}", username, role.name());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
