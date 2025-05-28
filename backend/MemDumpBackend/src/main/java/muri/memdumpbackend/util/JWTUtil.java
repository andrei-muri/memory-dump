package muri.memdumpbackend.util;

import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;

import muri.memdumpbackend.model.Role;
import muri.memdumpbackend.model.User;
import muri.memdumpbackend.repo.UserRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@AllArgsConstructor
public class JWTUtil {
    private final KeyUtil keyUtil;
    private final UserRepository userRepository;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(60 * 15 * 10)))
                .signWith(keyUtil.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(60 * 60 * 15)))
                .signWith(keyUtil.getPrivateKey(), Jwts.SIG.RS256)
                .compact();

    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(keyUtil.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public Role getRole(String token) {
        return Role.valueOf(extractClaims(token).get("role", String.class));
    }
}
