package com.campus.lostandfound.model;

import com.campus.lostandfound.constant.Constants;
import com.campus.lostandfound.exception.BusinessRuleViolationException;
import com.campus.lostandfound.model.base.BaseEntity;

import java.time.LocalDate;

/**
 * Main Entity 1: Represents an item reported lost by a student, staff, or visitor.
 */
public class LostItem extends BaseEntity {

    private String title;
    private String category;
    private String location;
    private LocalDate lostDate;
    private String brand;
    private String color;
    private String distinctiveFeatures;
    private double estimatedValue;
    private String reporterUsername;
    private String status;

    public LostItem() {
        super();
        this.status = Constants.STATUS_REPORTED;
    }

    public LostItem(String id, String title, String category, String location, LocalDate lostDate,
                    String brand, String color, String distinctiveFeatures, double estimatedValue,
                    String reporterUsername) {
        super(id);
        validate(title, category, location, lostDate, estimatedValue);
        this.title = title;
        this.category = category;
        this.location = location;
        this.lostDate = lostDate;
        this.brand = brand;
        this.color = color;
        this.distinctiveFeatures = distinctiveFeatures;
        this.estimatedValue = estimatedValue;
        this.reporterUsername = reporterUsername;
        this.status = Constants.STATUS_REPORTED;
    }

    /**
     * Enforces Business Rule 2: Validates report date and valuation constraints.
     */
    public static void validate(String title, String category, String location,
                                LocalDate lostDate, double estimatedValue) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Title cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Category is required.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Location is required.");
        }
        if (lostDate == null) {
            throw new BusinessRuleViolationException("BR-02-DATE", "Lost incident date is required.");
        }
        if (lostDate.isAfter(LocalDate.now())) {
            throw new BusinessRuleViolationException("BR-02-DATE",
                    "Invalid incident date: Lost item date cannot be in the future (" + lostDate + ").");
        }
        if (lostDate.isBefore(LocalDate.now().minusDays(Constants.MAX_REPORT_AGE_DAYS))) {
            throw new BusinessRuleViolationException("BR-02-DATE",
                    "Invalid incident date: Lost item date cannot be older than 365 days (" + lostDate + ").");
        }
        if (estimatedValue < 0) {
            throw new BusinessRuleViolationException("BR-02-VAL",
                    "Estimated monetary value cannot be negative: " + estimatedValue);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getLostDate() {
        return lostDate;
    }

    public void setLostDate(LocalDate lostDate) {
        this.lostDate = lostDate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDistinctiveFeatures() {
        return distinctiveFeatures;
    }

    public void setDistinctiveFeatures(String distinctiveFeatures) {
        this.distinctiveFeatures = distinctiveFeatures;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHighValue() {
        return estimatedValue >= Constants.HIGH_VALUE_THRESHOLD;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Cat: %s | Loc: %s | Date: %s | Val: $%.2f | Status: %s",
                getId(), title, category, location, lostDate, estimatedValue, status);
    }
}
