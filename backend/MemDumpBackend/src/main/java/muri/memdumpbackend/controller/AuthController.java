package muri.memdumpbackend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import muri.memdumpbackend.dto.SimpleMessageResponse;
import muri.memdumpbackend.dto.changepass.ChangePasswordRequest;
import muri.memdumpbackend.dto.login.LoginRequest;
import muri.memdumpbackend.dto.login.LoginResponse;
import muri.memdumpbackend.dto.refresh.RefreshResponse;
import muri.memdumpbackend.dto.register.RegisterRequest;
import muri.memdumpbackend.dto.resetpass.ResetPasswordRequest;
import muri.memdumpbackend.service.AuthService;
import muri.memdumpbackend.service.MailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;
    private final MailService mailService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthService.LoginResult loginResult = authService.login(loginRequest);
        response.addCookie(loginResult.refreshTokenCookie());
        return loginResult.response();
    }

    @PostMapping("/register")
    public SimpleMessageResponse register(@RequestBody RegisterRequest dto) {
        authService.register(dto);
        return new SimpleMessageResponse("Registered successfully");
    }

    @GetMapping("/refresh")
    public RefreshResponse refresh() {
        String token = authService.refresh();
        return new RefreshResponse(token);
    }

    @PostMapping("/logout")
    public SimpleMessageResponse logout(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/auth/refresh");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setAttribute("SameSite", "None");
        response.addCookie(refreshTokenCookie);
        return new SimpleMessageResponse("Logout successfully");
    }

    @PostMapping("/changepass")
    public SimpleMessageResponse changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest);
        return new SimpleMessageResponse("Change password successfully");
    }

    @PostMapping("/mail")
    public SimpleMessageResponse sendRecoveryMail(@RequestParam String email) {
        mailService.sendMail(email);
        return new SimpleMessageResponse("Mail sent successfully");
    }

    @PostMapping("/reset")
    public SimpleMessageResponse resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return new SimpleMessageResponse("Password reset successfully");
    }
}
