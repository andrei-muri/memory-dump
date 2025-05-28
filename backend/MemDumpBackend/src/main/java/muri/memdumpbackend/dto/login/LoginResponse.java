package muri.memdumpbackend.dto.login;

public record LoginResponse(String token, String id, String username, String email) {
}
