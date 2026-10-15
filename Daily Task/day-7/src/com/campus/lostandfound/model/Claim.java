package com.campus.lostandfound.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * Model: Claim
 * ============================================================================
 * Represents an ownership claim submitted by a campus member for a found item (FR3).
 *
 * COLLECTIONS FRAMEWORK INTEGRATION:
 * 1. Stored in java.util.ArrayDeque (Standard FIFO Review Queue):
 *    - Circular array buffer providing O(1) amortized enqueue/dequeue without
 *      allocating wrapper Node objects for student and guest claims.
 * 2. Stored in java.util.PriorityQueue (Faculty-First Expedited Queue):
 *    - Binary min-heap backed by an array.
 *    - Implements java.lang.Comparable<Claim> to prioritize Faculty (Priority 1)
 *      and Staff (Priority 2) claims ahead of Student (Priority 3) claims,
 *      breaking ties chronologically by submission date.
 * 3. De-duplicated in java.util.LinkedHashSet:
 *    - Composite key "CLAIMANT_USERNAME:FOUND_ITEM_ID" prevents duplicate
 *      active claims for the same item.
 * 4. Safe Eviction via java.util.Iterator.remove():
 *    - Expired or withdrawn claims can be purged safely during queue iteration
 *      without causing ConcurrentModificationException.
 */
public class Claim extends BaseEntity implements Comparable<Claim> {

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        EXPIRED
    }

    private String foundItemId;
    private String claimantUsername;
    private UserRole claimantRole;
    private String quizAnswerSecret;
    private String proofDescription;
    private Status status;
    private LocalDate claimDate;
    private LocalDate expiryDate; // Date by which claimant must provide proof
    private String reviewedBy;
    private String reviewRemarks;

    public Claim() {
        super();
        this.status = Status.PENDING;
        this.claimDate = LocalDate.now();
        this.expiryDate = this.claimDate.plusDays(7); // 7-day claim window
        this.claimantRole = UserRole.STUDENT;
    }

    public Claim(String id, String foundItemId, String claimantUsername, UserRole claimantRole,
                 String quizAnswerSecret, String proofDescription) {
        super(id);
        this.foundItemId = foundItemId;
        this.claimantUsername = claimantUsername;
        this.claimantRole = claimantRole != null ? claimantRole : UserRole.STUDENT;
        this.quizAnswerSecret = quizAnswerSecret;
        this.proofDescription = proofDescription;
        this.status = Status.PENDING;
        this.claimDate = LocalDate.now();
        this.expiryDate = this.claimDate.plusDays(7);
    }

    public Claim(String id, String foundItemId, String claimantUsername, UserRole claimantRole,
                 String quizAnswerSecret, String proofDescription, LocalDate claimDate, int validityDays) {
        super(id);
        this.foundItemId = foundItemId;
        this.claimantUsername = claimantUsername;
        this.claimantRole = claimantRole != null ? claimantRole : UserRole.STUDENT;
        this.quizAnswerSecret = quizAnswerSecret;
        this.proofDescription = proofDescription;
        this.status = Status.PENDING;
        this.claimDate = claimDate != null ? claimDate : LocalDate.now();
        this.expiryDate = this.claimDate.plusDays(validityDays);
    }

    // Accessors and Mutators
    public String getFoundItemId() { return foundItemId; }
    public void setFoundItemId(String foundItemId) { this.foundItemId = foundItemId; }

    public String getClaimantUsername() { return claimantUsername; }
    public void setClaimantUsername(String claimantUsername) { this.claimantUsername = claimantUsername; }

    public UserRole getClaimantRole() { return claimantRole; }
    public void setClaimantRole(UserRole claimantRole) { this.claimantRole = claimantRole; }

    public String getQuizAnswerSecret() { return quizAnswerSecret; }
    public void setQuizAnswerSecret(String quizAnswerSecret) { this.quizAnswerSecret = quizAnswerSecret; }

    public String getProofDescription() { return proofDescription; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewRemarks() { return reviewRemarks; }
    public void setReviewRemarks(String reviewRemarks) { this.reviewRemarks = reviewRemarks; }

    /**
     * Deduplication Key: Enforces 1 active claim per claimant per found item in LinkedHashSet.
     */
    public String getDeduplicationKey() {
        return claimantUsername + ":" + foundItemId;
    }

    /**
     * Checks if this claim is overdue / expired.
     */
    public boolean isOverdue(LocalDate currentReferenceDate) {
        return expiryDate != null
                && currentReferenceDate.isAfter(expiryDate)
                && status == Status.PENDING;
    }

    /**
     * Natural Ordering for PriorityQueue (Min-Heap):
     * 1. Claimant Role Priority Level: Faculty (1) < Staff (2) < Student (3) < Guest (4)
     * 2. Claim Date: Earliest date first (FIFO within the same role tier)
     * 3. ID: Unique tie breaker
     */
    @Override
    public int compareTo(Claim other) {
        if (other == null) return -1;

        // Primary: Role priority level (lower number = higher priority)
        int roleDiff = Integer.compare(
                this.claimantRole.getPriorityLevel(),
                other.claimantRole.getPriorityLevel()
        );
        if (roleDiff != 0) {
            return roleDiff;
        }

        // Secondary: Earliest claim date
        int dateDiff = this.claimDate.compareTo(other.claimDate);
        if (dateDiff != 0) {
            return dateDiff;
        }

        // Tertiary: Unique ID tie breaker
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return String.format("[%s] Claim on Item: %s | By: %s (%s) | Date: %s | Expires: %s | Status: %s",
                getId(), foundItemId, claimantUsername, claimantRole, claimDate, expiryDate, status);
    }
}
