package com.campus.lostandfound.claim.model;

import com.campus.lostandfound.common.model.Dtos.VerificationAnswer;

import java.time.Instant;
import java.util.List;

public record Claim(
        String id,
        String foundItemId,
        String lostReportId,
        String claimantUserId,
        String claimantName,
        String claimantContact,
        List<VerificationAnswer> answers,
        String proofAttachmentUrl,
        String status, // PENDING_VERIFICATION, UNDER_REVIEW, APPROVED, REJECTED, HANDED_OVER
        String officerReviewNotes,
        String reviewedByOfficerId,
        Instant submittedAt,
        Instant reviewedAt
) {
    public Claim withStatus(String newStatus, String notes, String officerId) {
        return new Claim(id, foundItemId, lostReportId, claimantUserId, claimantName, claimantContact, answers, proofAttachmentUrl, newStatus, notes, officerId, submittedAt, Instant.now());
    }
}
