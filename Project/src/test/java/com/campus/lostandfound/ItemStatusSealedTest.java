package com.campus.lostandfound;

import com.campus.lostandfound.common.status.ItemStatus;

public class ItemStatusSealedTest {

    public static void run() {
        System.out.println("--- Running ItemStatusSealedTest ---");

        ItemStatus[] states = new ItemStatus[]{
                new ItemStatus.Reported(),
                new ItemStatus.InCustody("BIN-1", "OFFICER-1"),
                new ItemStatus.UnderReview("DESK-A"),
                new ItemStatus.Matched(java.time.Instant.now(), "LOST-1", "FND-1", 0.95),
                new ItemStatus.ClaimPending("CLM-1", "USR-1"),
                new ItemStatus.ClaimApproved("CLM-1", "OFFICER-1"),
                new ItemStatus.HandedOver("HND-1", "RECIPIENT-1", "OFFICER-1"),
                new ItemStatus.ExpiredForDonation(62, "BATCH-9"),
                new ItemStatus.Rejected("Inconclusive ownership proof", "OFFICER-1")
        };

        for (ItemStatus status : states) {
            String desc;
            if (status instanceof ItemStatus.Reported r) {
                desc = "Reported at " + r.timestamp();
            } else if (status instanceof ItemStatus.InCustody c) {
                desc = "In locker " + c.lockerId();
            } else if (status instanceof ItemStatus.UnderReview u) {
                desc = "Under review by " + u.assignedTo();
            } else if (status instanceof ItemStatus.Matched m) {
                desc = "Matched score: " + m.matchScore();
            } else if (status instanceof ItemStatus.ClaimPending cp) {
                desc = "Claim pending: " + cp.claimId();
            } else if (status instanceof ItemStatus.ClaimApproved ca) {
                desc = "Claim approved by: " + ca.approvedByOfficerId();
            } else if (status instanceof ItemStatus.HandedOver h) {
                desc = "Handed over: " + h.handoverId();
            } else if (status instanceof ItemStatus.ExpiredForDonation e) {
                desc = "Expired after " + e.daysUnclaimed() + " days";
            } else if (status instanceof ItemStatus.Rejected rj) {
                desc = "Rejected: " + rj.reason();
            } else {
                throw new AssertionError("Unknown sealed type: " + status);
            }

            assert desc != null && !desc.isBlank() : "Status description must not be empty";
            assert status.code() != null : "Status code must not be null";

            // Verify reverse conversion fromCode
            ItemStatus restored = ItemStatus.fromCode(status.code());
            assert restored.code().equals(status.code()) : "Restored code mismatch: " + restored.code();
        }

        System.out.println("  [PASS] ItemStatusSealedTest: Sealed hierarchy, pattern matching, and record states verified successfully.");
    }
}