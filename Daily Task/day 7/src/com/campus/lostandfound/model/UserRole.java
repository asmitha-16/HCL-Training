package com.campus.lostandfound.model;

/**
 * ============================================================================
 * Enum: UserRole
 * ============================================================================
 * Defines campus stakeholder roles and their associated priority weighting.
 *
 * COLLECTIONS FRAMEWORK INTEGRATION:
 * - Backs the Faculty-First priority sorting in java.util.PriorityQueue:
 *     1. FACULTY (Priority 1) -> Expedited review for faculty research/teaching equipment.
 *     2. STAFF   (Priority 2) -> High institutional priority.
 *     3. STUDENT (Priority 3) -> Standard queue priority.
 *     4. GUEST   (Priority 4) -> External campus visitors.
 */
public enum UserRole {
    FACULTY(1, "Faculty Member / Professor"),
    STAFF(2, "University Staff / Administrator"),
    STUDENT(3, "Enrolled Student"),
    GUEST(4, "Campus Visitor");

    private final int priorityLevel;
    private final String description;

    UserRole(int priorityLevel, String description) {
        this.priorityLevel = priorityLevel;
        this.description = description;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public String getDescription() {
        return description;
    }
}
