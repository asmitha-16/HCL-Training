package com.campus.lostandfound.auth.model;

import java.time.Instant;

public record AppUser(
        String id,
        String username,
        String fullName,
        String email,
        Role role,
        String department,
        String phoneNumber,
        Instant createdAt
) {
    public AppUser(String id, String username, String fullName, String email, Role role, String department, String phoneNumber) {
        this(id, username, fullName, email, role, department, phoneNumber, Instant.now());
    }
}
