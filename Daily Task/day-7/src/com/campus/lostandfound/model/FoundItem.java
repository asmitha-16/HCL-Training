package com.campus.lostandfound.model;

import java.time.LocalDate;

/**
 * ============================================================================
 * Model: FoundItem
 * ============================================================================
 * Represents a physical item found on campus and secured in custody (FR2).
 *
 * COLLECTIONS FRAMEWORK INTEGRATION:
 * 1. Stored in java.util.LinkedHashSet (FR2 Custody Registry):
 *    - Guarantees uniqueness: Prevents duplicate custody entries (O(1) add/contains)
 *      keyed on BaseEntity ID.
 *    - Preserves exact insertion order: Unlike HashSet which scrambles ordering,
 *      LinkedHashSet maintains a doubly-linked list across hash buckets to reflect
 *      the chronological security intake sequence.
 * 2. Overdue Retention & Safe Eviction via java.util.Iterator.remove():
 *    - Found items remaining unclaimed beyond the retention threshold (30 days)
 *      are marked overdue.
 *    - Safely purged using Iterator.remove() to avoid ConcurrentModificationException.
 * 3. Implements java.lang.Comparable<FoundItem>:
 *    - Allows alphabetical or chronological sorting in java.util.TreeSet.
 */
public class FoundItem extends BaseEntity implements Comparable<FoundItem> {

    public static final int DEFAULT_RETENTION_DAYS = 30;

    private String title;
    private String category;
    private String location;
    private LocalDate foundDate;
    private String lockerNumber;
    private String secretIdentificationFeature; // Confidential attribute used for verification quiz
    private String finderUsername;
    private String status; // "IN_CUSTODY", "CLAIM_PENDING", "CLAIMED", "EXPIRED_AUCTIONED"
    private LocalDate expiryDate;
    private String approvedClaimId;

    public FoundItem() {
        super();
        this.status = "IN_CUSTODY";
        this.foundDate = LocalDate.now();
        this.expiryDate = this.foundDate.plusDays(DEFAULT_RETENTION_DAYS);
    }

    public FoundItem(String id, String title, String category, String location, LocalDate foundDate,
                     String lockerNumber, String secretIdentificationFeature, String finderUsername) {
        super(id);
        this.title = title;
        this.category = category;
        this.location = location;
        this.foundDate = foundDate != null ? foundDate : LocalDate.now();
        this.lockerNumber = lockerNumber;
        this.secretIdentificationFeature = secretIdentificationFeature;
        this.finderUsername = finderUsername;
        this.status = "IN_CUSTODY";
        this.expiryDate = this.foundDate.plusDays(DEFAULT_RETENTION_DAYS);
    }

    public FoundItem(String id, String title, String category, String location, LocalDate foundDate,
                     String lockerNumber, String secretIdentificationFeature, String finderUsername,
                     int retentionDays) {
        super(id);
        this.title = title;
        this.category = category;
        this.location = location;
        this.foundDate = foundDate != null ? foundDate : LocalDate.now();
        this.lockerNumber = lockerNumber;
        this.secretIdentificationFeature = secretIdentificationFeature;
        this.finderUsername = finderUsername;
        this.status = "IN_CUSTODY";
        this.expiryDate = this.foundDate.plusDays(retentionDays);
    }

    // Accessors and Mutators
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getFoundDate() { return foundDate; }
    public void setFoundDate(LocalDate foundDate) {
        this.foundDate = foundDate;
        this.expiryDate = foundDate != null ? foundDate.plusDays(DEFAULT_RETENTION_DAYS) : null;
    }

    public String getLockerNumber() { return lockerNumber; }
    public void setLockerNumber(String lockerNumber) { this.lockerNumber = lockerNumber; }

    public String getSecretIdentificationFeature() { return secretIdentificationFeature; }
    public void setSecretIdentificationFeature(String secretIdentificationFeature) {
        this.secretIdentificationFeature = secretIdentificationFeature;
    }

    public String getFinderUsername() { return finderUsername; }
    public void setFinderUsername(String finderUsername) { this.finderUsername = finderUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getApprovedClaimId() { return approvedClaimId; }
    public void setApprovedClaimId(String approvedClaimId) { this.approvedClaimId = approvedClaimId; }

    /**
     * Checks if this found item is overdue (unclaimed past retention threshold).
     */
    public boolean isOverdue(LocalDate currentReferenceDate) {
        return expiryDate != null
                && currentReferenceDate.isAfter(expiryDate)
                && "IN_CUSTODY".equalsIgnoreCase(status);
    }

    /**
     * Comparable implementation: Alphabetical by title, then ID tie-breaker.
     * Used by java.util.TreeSet.
     */
    @Override
    public int compareTo(FoundItem other) {
        if (other == null) return -1;
        int titleCompare = this.title.compareToIgnoreCase(other.title);
        if (titleCompare != 0) {
            return titleCompare;
        }
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" | Cat: %s | Loc: %s | Found: %s | Locker: %s | Status: %s | Expires: %s",
                getId(), title, category, location, foundDate, lockerNumber, status, expiryDate);
    }
}
