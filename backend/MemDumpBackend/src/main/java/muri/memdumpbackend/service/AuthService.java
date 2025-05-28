package muri.memdumpbackend.service;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import muri.memdumpbackend.dto.changepass.ChangePasswordRequest;
import muri.memdumpbackend.dto.login.LoginRequest;
import muri.memdumpbackend.dto.login.LoginResponse;
import muri.memdumpbackend.dto.register.RegisterRequest;
import muri.memdumpbackend.dto.resetpass.ResetPasswordRequest;
import muri.memdumpbackend.exception.AuthException;
import muri.memdumpbackend.model.User;
import muri.memdumpbackend.repo.UserRepository;
import muri.memdumpbackend.util.JWTUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest dto) {
        userRepository.findByUsername(dto.username()).ifPresent((User user) -> { throw new AuthException("Username " + user.getUsername() + " already exists"); });
        userRepository.findByEmail(dto.email()).ifPresent((User user) -> { throw new AuthException("Email " + user.getEmail() + " already exists"); });
        User user = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .profile(null)
                .recovery(null)
                .build();

        userRepository.save(user);
    }

    public record LoginResult(LoginResponse response, Cookie refreshTokenCookie) {}

    public LoginResult login(LoginRequest loginRequest) {
        log.debug("Processing login for username: {}", loginRequest.username());

        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found for username: {}", loginRequest.username());
                    return new AuthException("Invalid username or password");
                });

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            log.warn("Login failed: Incorrect password for username: {}", loginRequest.username());
            throw new AuthException("Invalid username or password");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        log.info("Login successful for email: {}. Generated access and refresh tokens", user.getEmail());

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/auth/refresh");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setAttribute("SameSite", "None");

        LoginResponse response = new LoginResponse(accessToken, user.getId().toString(), user.getUsername(), user.getEmail());
        return new LoginResult(response, refreshTokenCookie);
    }

    public String refresh() {
        String loggedUsername =  SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedUser = userRepository.findByUsername(loggedUsername)
                .orElseThrow(() -> new AuthException("Username " + loggedUsername + " does not exist"));
        return jwtUtil.generateAccessToken(loggedUser);
    }

    public void changePassword(ChangePasswordRequest dto) {
        String loggedUsername =  SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new AuthException("Username " + loggedUsername + " does not exist"));
        if (!loggedUsername.equalsIgnoreCase(user.getUsername())) {
            throw new AuthException("Invalid username");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new AuthException("Invalid old password");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new AuthException("New passwords do not match");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new AuthException("New password must be different from old password");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public void resetPassword(ResetPasswordRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException("Email " + dto.getEmail() + " does not exist"));
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new AuthException("New passwords do not match");
        }
        if (user.getRecovery() == null) {
            user.setRecovery(null);
            throw new AuthException("No token issued for this mail");
        } else if (!dto.getToken().equals(user.getRecovery().getToken())) {
            user.setRecovery(null);
            throw new AuthException("Token mismatch");
        } else if (user.getRecovery().getExpiration().isBefore(LocalDateTime.now())) {
            user.setRecovery(null);
            throw new AuthException("Token expired");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setRecovery(null);
        userRepository.save(user);

    }

}
