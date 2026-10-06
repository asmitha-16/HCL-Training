package com.campus.lostandfound.common.status;

import java.time.Instant;

/**
 * Sealed interface representing the lifecycle states of an item in the Lost and Found system.
 * Enforces a closed, compiler-checked hierarchy for pattern matching.
 */
public sealed interface ItemStatus permits 
        ItemStatus.Reported,
        ItemStatus.InCustody,
        ItemStatus.UnderReview,
        ItemStatus.Matched,
        ItemStatus.ClaimPending,
        ItemStatus.ClaimApproved,
        ItemStatus.HandedOver,
        ItemStatus.ExpiredForDonation,
        ItemStatus.Rejected {

    String code();
    String displayName();
    Instant timestamp();

    record Reported(Instant timestamp, String notes) implements ItemStatus {
        public Reported() { this(Instant.now(), "Report logged"); }
        @Override public String code() { return "REPORTED"; }
        @Override public String displayName() { return "Reported (Open)"; }
    }

    record InCustody(Instant timestamp, String lockerId, String loggedBy) implements ItemStatus {
        public InCustody(String lockerId, String loggedBy) { this(Instant.now(), lockerId, loggedBy); }
        @Override public String code() { return "IN_CUSTODY"; }
        @Override public String displayName() { return "In Security Custody"; }
    }

    record UnderReview(Instant timestamp, String assignedTo) implements ItemStatus {
        public UnderReview(String assignedTo) { this(Instant.now(), assignedTo); }
        @Override public String code() { return "UNDER_REVIEW"; }
        @Override public String displayName() { return "Under Review"; }
    }

    record Matched(Instant timestamp, String lostReportId, String foundItemId, double matchScore) implements ItemStatus {
        @Override public String code() { return "MATCHED"; }
        @Override public String displayName() { return "Match Suggested"; }
    }

    record ClaimPending(Instant timestamp, String claimId, String claimantId) implements ItemStatus {
        public ClaimPending(String claimId, String claimantId) { this(Instant.now(), claimId, claimantId); }
        @Override public String code() { return "CLAIM_PENDING"; }
        @Override public String displayName() { return "Claim Pending Verification"; }
    }

    record ClaimApproved(Instant timestamp, String claimId, String approvedByOfficerId) implements ItemStatus {
        public ClaimApproved(String claimId, String approvedByOfficerId) { this(Instant.now(), claimId, approvedByOfficerId); }
        @Override public String code() { return "CLAIM_APPROVED"; }
        @Override public String displayName() { return "Claim Approved"; }
    }

    record HandedOver(Instant timestamp, String handoverId, String recipientId, String officerId) implements ItemStatus {
        public HandedOver(String handoverId, String recipientId, String officerId) { this(Instant.now(), handoverId, recipientId, officerId); }
        @Override public String code() { return "HANDED_OVER"; }
        @Override public String displayName() { return "Handed Over (Resolved)"; }
    }

    record ExpiredForDonation(Instant timestamp, long daysUnclaimed, String batchReference) implements ItemStatus {
        public ExpiredForDonation(long daysUnclaimed, String batchReference) { this(Instant.now(), daysUnclaimed, batchReference); }
        @Override public String code() { return "EXPIRED_FOR_DONATION"; }
        @Override public String displayName() { return "Flagged for Donation (60+ Days)"; }
    }

    record Rejected(Instant timestamp, String reason, String rejectedBy) implements ItemStatus {
        public Rejected(String reason, String rejectedBy) { this(Instant.now(), reason, rejectedBy); }
        @Override public String code() { return "REJECTED"; }
        @Override public String displayName() { return "Claim/Item Rejected"; }
    }

    static ItemStatus fromCode(String code) {
        if (code == null) return new Reported();
        return switch (code.toUpperCase()) {
            case "REPORTED" -> new Reported();
            case "IN_CUSTODY" -> new InCustody("DEFAULT-LOCKER", "SYSTEM");
            case "UNDER_REVIEW" -> new UnderReview("SECURITY");
            case "MATCHED" -> new Matched(Instant.now(), "N/A", "N/A", 1.0);
            case "CLAIM_PENDING" -> new ClaimPending("N/A", "N/A");
            case "CLAIM_APPROVED" -> new ClaimApproved("N/A", "SECURITY");
            case "HANDED_OVER" -> new HandedOver("N/A", "N/A", "SECURITY");
            case "EXPIRED_FOR_DONATION" -> new ExpiredForDonation(60, "BATCH-AUTO");
            case "REJECTED" -> new Rejected("Standard rejection", "SECURITY");
            default -> new Reported(Instant.now(), code);
        };
    }
}
