package com.campus.lostandfound.auth.model;

public enum Role {
    ADMIN("Admin", "Full administrative access, recovery rate metrics, audit trail review, expiry management"),
    SECURITY_DESK("Security Desk", "Found item intake, photo privacy masking, verification review, physical handover"),
    STUDENT_STAFF("Student/Staff", "Report lost items, browse public found items, submit ownership claims");

    private final String title;
    private final String description;

    Role(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public static Role fromString(String val) {
        if (val == null) return STUDENT_STAFF;
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(val) || r.title.equalsIgnoreCase(val)) {
                return r;
            }
        }
        return STUDENT_STAFF;
    }
}
