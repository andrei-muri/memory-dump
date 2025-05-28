package muri.memdumpbackend.dto.register;

import muri.memdumpbackend.model.Role;

public record RegisterRequest(String username, String password, String email, Role role) {
}
