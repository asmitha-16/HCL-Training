package com.campus.lostandfound.auth.service;

import com.campus.lostandfound.auth.model.AppUser;
import com.campus.lostandfound.auth.model.Role;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private final Map<String, AppUser> usersById = new ConcurrentHashMap<>();
    private final Map<String, AppUser> usersByUsername = new ConcurrentHashMap<>();
    private final Map<String, String> tokenToUserId = new ConcurrentHashMap<>();

    public AuthService() {
        seedDefaultUsers();
    }

    private void seedDefaultUsers() {
        registerUser(new AppUser("USR-ADM-01", "admin", "Dr. Eleanor Vance", "admin@campus.edu", Role.ADMIN, "Campus Safety Administration", "+1-555-0100"));
        registerUser(new AppUser("USR-SEC-01", "officer_dan", "Officer Daniel Hayes", "sec.dan@campus.edu", Role.SECURITY_DESK, "Central Security Desk - South Gate", "+1-555-0144"));
        registerUser(new AppUser("USR-SEC-02", "officer_sarah", "Officer Sarah Miller", "sec.sarah@campus.edu", Role.SECURITY_DESK, "Library Security Point", "+1-555-0145"));
        registerUser(new AppUser("USR-STU-01", "student_asmitha", "Asmitha B.", "asmitha.b@student.campus.edu", Role.STUDENT_STAFF, "Dept of Computer Science", "+1-555-0189"));
        registerUser(new AppUser("USR-STU-02", "student_rahul", "Rahul Sharma", "rahul.s@student.campus.edu", Role.STUDENT_STAFF, "Electrical Engineering", "+1-555-0192"));
        registerUser(new AppUser("USR-STF-01", "prof_kumar", "Prof. Rajesh Kumar", "kumar.r@faculty.campus.edu", Role.STUDENT_STAFF, "Department of Physics", "+1-555-0111"));
    }

    public synchronized AppUser registerUser(AppUser user) {
        usersById.put(user.id(), user);
        usersByUsername.put(user.username().toLowerCase(), user);
        return user;
    }

    public Optional<AppUser> getUserById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Optional<AppUser> getUserByUsername(String username) {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(usersByUsername.get(username.toLowerCase()));
    }

    public List<AppUser> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }

    public String login(String username) {
        Optional<AppUser> user = getUserByUsername(username);
        if (user.isPresent()) {
            String token = "tok_" + UUID.randomUUID().toString().replace("-", "");
            tokenToUserId.put(token, user.get().id());
            return token;
        }
        throw new IllegalArgumentException("Unknown username: " + username);
    }

    public Optional<AppUser> validateToken(String token) {
        if (token == null) return Optional.empty();
        String userId = tokenToUserId.get(token);
        if (userId != null) {
            return getUserById(userId);
        }
        return Optional.empty();
    }
}
