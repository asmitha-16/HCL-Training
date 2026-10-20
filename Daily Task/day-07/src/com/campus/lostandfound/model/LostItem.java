package com.campus.lostandfound.model;

import java.time.LocalDate;

/**
 * ============================================================================
 * Model: LostItem
 * ============================================================================
 * Represents a lost item reported by a student, faculty member, or staff (FR1).
 *
 * COLLECTIONS FRAMEWORK INTEGRATION:
 * 1. Stored in java.util.ArrayList (FR1 Catalogue):
 *    - Contiguous array storage provides O(1) indexed random access via get(i),
 *      making sequential catalogue browsing and pagination fast.
 *    - CPU cache spatial locality ensures iterating through lost items is
 *      dramatically faster than pointer-chasing in LinkedList.
 * 2. Implements java.lang.Comparable<LostItem>:
 *    - Allows natural chronological sorting (newest first) when placed in
 *      java.util.TreeSet.
 */
public class LostItem extends BaseEntity implements Comparable<LostItem> {

    private String title;
    private String category;
    private String location;
    private LocalDate lostDate;
    private String brand;
    private String color;
    private String distinctiveFeatures;
    private double estimatedValue;
    private String reporterUsername;
    private String status; // "REPORTED", "MATCHED", "RESOLVED", "CANCELLED"

    public LostItem() {
        super();
        this.status = "REPORTED";
    }

    public LostItem(String id, String title, String category, String location, LocalDate lostDate,
                    String brand, String color, String distinctiveFeatures, double estimatedValue,
                    String reporterUsername) {
        super(id);
        this.title = title;
        this.category = category;
        this.location = location;
        this.lostDate = lostDate != null ? lostDate : LocalDate.now();
        this.brand = brand;
        this.color = color;
        this.distinctiveFeatures = distinctiveFeatures;
        this.estimatedValue = estimatedValue;
        this.reporterUsername = reporterUsername;
        this.status = "REPORTED";
    }

    // Accessors and Mutators
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getLostDate() { return lostDate; }
    public void setLostDate(LocalDate lostDate) { this.lostDate = lostDate; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDistinctiveFeatures() { return distinctiveFeatures; }
    public void setDistinctiveFeatures(String distinctiveFeatures) { this.distinctiveFeatures = distinctiveFeatures; }

    public double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(double estimatedValue) { this.estimatedValue = estimatedValue; }

    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Comparable implementation: Sorts by lostDate descending (newest first).
     * Used by java.util.TreeSet.
     */
    @Override
    public int compareTo(LostItem other) {
        if (other == null) return -1;
        int dateCompare = other.lostDate.compareTo(this.lostDate); // newest first
        if (dateCompare != 0) {
            return dateCompare;
        }
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" | Cat: %s | Location: %s | Lost: %s | Val: $%.2f | By: %s | Status: %s",
                getId(), title, category, location, lostDate, estimatedValue, reporterUsername, status);
    }
}
