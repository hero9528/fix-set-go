package com.fixsetgo.auth;

public record AuthResponse(
        String token,
        UserProfile user
) {
}
