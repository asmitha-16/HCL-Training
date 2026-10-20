import java.util.Objects;

/**
 * HandoverReceipt.java
 *
 * Day 11 Project Deliverable: Generated Entity Model (FR5, FR8)
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Encapsulates verified physical handover transactions generated at the Security Desk.
 */
public class HandoverReceipt {

    private final String receiptId;
    private final String foundItemId;
    private final String claimId;
    private final String claimantId;
    private final String securityOfficerId;
    private final String verifiedIdType; // "STUDENT_ID", "EMPLOYEE_ID", "GOVT_ID"
    private final long timestamp;

    public HandoverReceipt(String receiptId, String foundItemId, String claimId,
                           String claimantId, String securityOfficerId, String verifiedIdType) {
        if (receiptId == null || receiptId.isBlank()) throw new IllegalArgumentException("Receipt ID cannot be blank");
        if (foundItemId == null || foundItemId.isBlank()) throw new IllegalArgumentException("Found Item ID required");
        if (claimId == null || claimId.isBlank()) throw new IllegalArgumentException("Claim ID required");

        this.receiptId = receiptId.trim();
        this.foundItemId = foundItemId.trim();
        this.claimId = claimId.trim();
        this.claimantId = (claimantId != null) ? claimantId.trim() : "UNKNOWN";
        this.securityOfficerId = (securityOfficerId != null) ? securityOfficerId.trim() : "SEC-DESK";
        this.verifiedIdType = (verifiedIdType != null) ? verifiedIdType.trim() : "CAMPUS_STUDENT_ID";
        this.timestamp = System.currentTimeMillis();
    }

    public String getReceiptId() { return receiptId; }
    public String getFoundItemId() { return foundItemId; }
    public String getClaimId() { return claimId; }
    public String getClaimantId() { return claimantId; }
    public String getSecurityOfficerId() { return securityOfficerId; }
    public String getVerifiedIdType() { return verifiedIdType; }
    public long getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandoverReceipt that)) return false;
        return Objects.equals(receiptId, that.receiptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiptId);
    }

    @Override
    public String toString() {
        return String.format("HandoverReceipt[ID=%s, Item=%s, Claim=%s, Claimant=%s, Officer=%s]",
                receiptId, foundItemId, claimId, claimantId, securityOfficerId);
    }
}
