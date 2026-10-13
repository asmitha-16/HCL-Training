package com.campus.lostandfound.model;

import com.campus.lostandfound.constant.Constants;
import com.campus.lostandfound.exception.BusinessRuleViolationException;
import com.campus.lostandfound.model.base.BaseEntity;

import java.time.LocalDate;

/**
 * Main Entity 2: Represents a physical item discovered on campus and secured in a custody locker.
 */
public class FoundItem extends BaseEntity {

    private String title;
    private String category;
    private String location;
    private LocalDate foundDate;
    private String lockerNumber;
    private String secretIdentificationFeature; // Confidential attribute used for verification quiz
    private String photoReference;
    private String finderUsername;
    private String status;
    private LocalDate expiryDate;
    private String approvedClaimId;

    public FoundItem() {
        super();
        this.status = Constants.STATUS_IN_CUSTODY;
    }

    public FoundItem(String id, String title, String category, String location, LocalDate foundDate,
                     String lockerNumber, String secretIdentificationFeature, String finderUsername) {
        super(id);
        validate(title, category, location, foundDate, lockerNumber);
        this.title = title;
        this.category = category;
        this.location = location;
        this.foundDate = foundDate;
        this.lockerNumber = lockerNumber;
        this.secretIdentificationFeature = secretIdentificationFeature;
        this.finderUsername = finderUsername;
        this.status = Constants.STATUS_IN_CUSTODY;
        this.expiryDate = foundDate.plusDays(Constants.EXPIRY_DAYS_THRESHOLD);
    }

    /**
     * Enforces Business Rule 2: Validation on found item intake.
     */
    public static void validate(String title, String category, String location,
                                LocalDate foundDate, String lockerNumber) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Title cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Category is required.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Discovery location is required.");
        }
        if (foundDate == null) {
            throw new BusinessRuleViolationException("BR-02-DATE", "Found date is required.");
        }
        if (foundDate.isAfter(LocalDate.now())) {
            throw new BusinessRuleViolationException("BR-02-DATE",
                    "Invalid found date: Found date cannot be in the future (" + foundDate + ").");
        }
        if (lockerNumber == null || lockerNumber.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Assigned custody locker is required.");
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

    public LocalDate getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(LocalDate foundDate) {
        this.foundDate = foundDate;
        if (foundDate != null) {
            this.expiryDate = foundDate.plusDays(Constants.EXPIRY_DAYS_THRESHOLD);
        }
    }

    public String getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(String lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public String getSecretIdentificationFeature() {
        return secretIdentificationFeature;
    }

    public void setSecretIdentificationFeature(String secretIdentificationFeature) {
        this.secretIdentificationFeature = secretIdentificationFeature;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getFinderUsername() {
        return finderUsername;
    }

    public void setFinderUsername(String finderUsername) {
        this.finderUsername = finderUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getApprovedClaimId() {
        return approvedClaimId;
    }

    public void setApprovedClaimId(String approvedClaimId) {
        this.approvedClaimId = approvedClaimId;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isLockedForClaims() {
        return Constants.STATUS_CLAIM_APPROVED.equals(status)
                || Constants.STATUS_HANDED_OVER.equals(status)
                || Constants.STATUS_BATCHED_FOR_DONATION.equals(status);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Cat: %s | Locker: %s | Found: %s | Expires: %s | Status: %s",
                getId(), title, category, lockerNumber, foundDate, expiryDate, status);
    }
}
