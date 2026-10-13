package com.campus.lostandfound.model;

import com.campus.lostandfound.constant.Constants;
import com.campus.lostandfound.exception.BusinessRuleViolationException;
import com.campus.lostandfound.model.base.BaseEntity;

import java.time.LocalDate;

/**
 * Main Entity 3: Represents an ownership claim submitted by a claimant for a found item.
 */
public class Claim extends BaseEntity {

    private String foundItemId;
    private String claimantUsername;
    private String quizAnswerSecret; // The claimant's answer to the verification question
    private String proofDescription;
    private String status;
    private LocalDate claimDate;
    private String reviewedBy;
    private String reviewRemarks;
    private LocalDate reviewedDate;

    public Claim() {
        super();
        this.status = Constants.STATUS_CLAIM_PENDING;
        this.claimDate = LocalDate.now();
    }

    public Claim(String id, String foundItemId, String claimantUsername,
                 String quizAnswerSecret, String proofDescription) {
        super(id);
        validate(foundItemId, claimantUsername, quizAnswerSecret);
        this.foundItemId = foundItemId;
        this.claimantUsername = claimantUsername;
        this.quizAnswerSecret = quizAnswerSecret;
        this.proofDescription = proofDescription;
        this.status = Constants.STATUS_CLAIM_PENDING;
        this.claimDate = LocalDate.now();
    }

    public static void validate(String foundItemId, String claimantUsername, String quizAnswerSecret) {
        if (foundItemId == null || foundItemId.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Target Found Item ID is required.");
        }
        if (claimantUsername == null || claimantUsername.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL", "Claimant username is required.");
        }
        if (quizAnswerSecret == null || quizAnswerSecret.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-02-VAL",
                    "Verification challenge answer is required for ownership authentication.");
        }
    }

    public String getFoundItemId() {
        return foundItemId;
    }

    public void setFoundItemId(String foundItemId) {
        this.foundItemId = foundItemId;
    }

    public String getClaimantUsername() {
        return claimantUsername;
    }

    public void setClaimantUsername(String claimantUsername) {
        this.claimantUsername = claimantUsername;
    }

    public String getQuizAnswerSecret() {
        return quizAnswerSecret;
    }

    public void setQuizAnswerSecret(String quizAnswerSecret) {
        this.quizAnswerSecret = quizAnswerSecret;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewRemarks() {
        return reviewRemarks;
    }

    public void setReviewRemarks(String reviewRemarks) {
        this.reviewRemarks = reviewRemarks;
    }

    public LocalDate getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(LocalDate reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    @Override
    public String toString() {
        return String.format("[%s] Claim on Item: %s by %s | Date: %s | Status: %s",
                getId(), foundItemId, claimantUsername, claimDate, status);
    }
}
