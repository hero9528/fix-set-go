package com.fixsetgo.auth;

import com.fixsetgo.user.AppUser;

public record UserProfile(
        Long id,
        String fullName,
        String email,
        String company,
        String role
) {
    public static UserProfile from(AppUser user) {
        return new UserProfile(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCompany(),
                user.getRole()
        );
    }
}
