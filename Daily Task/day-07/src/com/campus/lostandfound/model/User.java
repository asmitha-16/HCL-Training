package com.campus.lostandfound.model;

import java.util.Objects;

/**
 * ============================================================================
 * Model: User
 * ============================================================================
 * Represents a registered campus member (Faculty, Staff, or Student).
 */
public class User {

    private final String username;
    private final String fullName;
    private final String email;
    private final String department;
    private final UserRole role;

    public User(String username, String fullName, String email, String department, UserRole role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be blank");
        }
        this.username = username.trim().toLowerCase();
        this.fullName = fullName.trim();
        this.email = email != null ? email.trim() : "";
        this.department = department != null ? department.trim() : "General";
        this.role = role != null ? role : UserRole.STUDENT;
    }

    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public UserRole getRole() { return role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return String.format("%s (%s, Dept: %s, Username: %s)", fullName, role, department, username);
    }
}
